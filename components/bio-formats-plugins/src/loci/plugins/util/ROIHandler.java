/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.util;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.plugin.frame.RoiManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import ome.units.quantity.Length;
import ome.units.UNITS;
import ome.xml.model.Ellipse;
import ome.xml.model.OME;
import ome.xml.model.Point;
import ome.xml.model.Polygon;
import ome.xml.model.Polyline;
import ome.xml.model.Shape;
import ome.xml.model.Union;


// TODO: Stored ROIs are not correctly linked to Image.

/**
 * Utility class for managing regions of interest within ImageJ.
 * Capable of constructing ROIs within ImageJ's ROI manager matching
 * those specified in an OME metadata store, and vice versa.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ROIHandler {

    // -- ROIHandler API methods --

    /**
     * Look for ROIs in the given OMEXMLMetadata; if any are present, apply
     * them to the given images and display them in the ROI manager.
     */
    public static void openROIs(IMetadata retrieve, ImagePlus[] images) {
        if (!(retrieve instanceof OMEXMLMetadata)) return;
        int nextRoi = 0;
        RoiManager manager = RoiManager.getInstance();

        OME root = (OME) retrieve.getRoot();
        Color sc = null;
        Color fc = null;
        Float sw = null;

        int imageCount = images.length;
        for (int imageNum=0; imageNum<imageCount; imageNum++) {
            int roiCount = root.sizeOfROIList();
            if (roiCount > 0 && manager == null) {
                manager = new RoiManager();
            }
            for (int roiNum=0; roiNum<roiCount; roiNum++) {
                Union shapeSet = root.getROI(roiNum).getUnion();
                int shapeCount = shapeSet.sizeOfShapeList();

                for (int shape=0; shape<shapeCount; shape++) {
                    Shape shapeObject = shapeSet.getShape(shape);

                    Roi roi = null;

                    if (shapeObject instanceof Ellipse) {
                        Ellipse ellipse = (Ellipse) shapeObject;
                        int cx = ellipse.getX().intValue();
                        int cy = ellipse.getY().intValue();
                        int rx = ellipse.getRadiusX().intValue();
                        int ry = ellipse.getRadiusY().intValue();
                        roi = new OvalRoi(cx - rx, cy - ry, rx * 2, ry * 2);

                        if( ellipse.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = ellipse.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if(ellipse.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = ellipse.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if(ellipse.getStrokeWidth() != null){
                            sw = ellipse.getStrokeWidth().value().floatValue();
                        }
                    }
                    else if (shapeObject instanceof ome.xml.model.Line) {
                        ome.xml.model.Line line = (ome.xml.model.Line) shapeObject;
                        int x1 = line.getX1().intValue();
                        int x2 = line.getX2().intValue();
                        int y1 = line.getY1().intValue();
                        int y2 = line.getY2().intValue();
                        roi = new Line(x1, y1, x2, y2);

                        if(line.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = line.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if(line.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = line.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if(line.getStrokeWidth() != null){
                            sw = line.getStrokeWidth().value().floatValue();
                        }
                    }
                    else if (shapeObject instanceof Point) {
                        Point point = (Point) shapeObject;
                        int x = point.getX().intValue();
                        int y = point.getY().intValue();
                        roi = new OvalRoi(x, y, 0, 0);

                        if (point.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = point.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if (point.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = point.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if (point.getStrokeWidth() != null){
                            sw = point.getStrokeWidth().value().floatValue();
                        }
                    }
                    else if (shapeObject instanceof Polyline) {
                        Polyline polyline = (Polyline) shapeObject;
                        String points = polyline.getPoints();
                        int[][] coordinates = parsePoints(points);
                        roi = new PolygonRoi(coordinates[0], coordinates[1],
                                coordinates[0].length, Roi.POLYLINE);

                        if (polyline.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = polyline.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if (polyline.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = polyline.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if (polyline.getStrokeWidth() != null){
                            sw = polyline.getStrokeWidth().value().floatValue();
                        }
                    }
                    else if (shapeObject instanceof Polygon) {
                        Polygon polygon = (Polygon) shapeObject;
                        String points = polygon.getPoints();
                        int[][] coordinates = parsePoints(points);
                        roi = new PolygonRoi(coordinates[0], coordinates[1],
                                coordinates[0].length, Roi.POLYGON);

                        if (polygon.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = polygon.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if (polygon.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = polygon.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if (polygon.getStrokeWidth() != null){
                            sw = polygon.getStrokeWidth().value().floatValue();
                        }
                    }
                    else if (shapeObject instanceof ome.xml.model.Label){
                        //add support for TextROI's
                        ome.xml.model.Label label =
                                (ome.xml.model.Label) shapeObject;
                        double x = label.getX().doubleValue();
                        double y = label.getY().doubleValue();
                        String labelText = label.getText();

                        int size = label.getFontSize().value().intValue();
                        java.awt.Font font = new Font(labelText, Font.PLAIN, size);
                        roi = new TextRoi((int) x,(int) y, labelText,font);

                        if (label.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = label.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if (label.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = label.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if (label.getStrokeWidth() != null){
                            sw = label.getStrokeWidth().value().floatValue();
                        }

                    }
                    else if (shapeObject instanceof ome.xml.model.Rectangle) {
                        ome.xml.model.Rectangle rectangle =
                                (ome.xml.model.Rectangle) shapeObject;
                        int x = rectangle.getX().intValue();
                        int y = rectangle.getY().intValue();
                        int w = rectangle.getWidth().intValue();
                        int h = rectangle.getHeight().intValue();

                        roi = new Roi(x, y, w, h);

                        if (rectangle.getStrokeColor() != null){
                            ome.xml.model.primitives.Color StrokeColor = rectangle.getStrokeColor();
                            sc = new Color(StrokeColor.getGreen(),StrokeColor.getBlue(),StrokeColor.getAlpha(),StrokeColor.getRed());
                        }
                        if (rectangle.getFillColor() != null){
                            ome.xml.model.primitives.Color FillColor = rectangle.getFillColor();
                            fc = new Color(FillColor.getGreen(),FillColor.getBlue(),FillColor.getAlpha(),FillColor.getRed());
                        }
                        if (rectangle.getStrokeWidth() != null){
                            sw = rectangle.getStrokeWidth().value().floatValue();
                        }
                    }


                    if (roi != null) {

                        roi.setImage(images[imageNum]);

                        if (sw != null) {
                            if(sw==0) sw=(float) 1; 
                            roi.setStrokeWidth(sw);

                        }
                        if (sc != null) roi.setStrokeColor(sc);
                        manager.add(images[imageNum], roi, nextRoi++);
                        manager.runCommand("Select All");
                        manager.runCommand("Show All");
                    }
                }
            }
        }
    }

    public static Roi[] readFromRoiManager(){

        RoiManager manager = RoiManager.getInstance();
        if (manager == null) return null;
        Roi[] rois = manager.getRoisAsArray();
        return rois;
    }

    /** Save ROIs in the ROI manager to the given MetadataStore. */
    public static void saveROIs(MetadataStore store) {
        Roi[] rois = readFromRoiManager();

        if (rois == null || rois.length == 0) return;
        List<String> discardList = new ArrayList<String>();
        String roiID = null;
        OME root = (OME) store.getRoot();
        int roicount = root.sizeOfROIList();
        int cntr = roicount;
        for (int i=0; i<rois.length; i++) {

            String polylineID = MetadataTools.createLSID("Shape", cntr, 0);
            roiID = MetadataTools.createLSID("ROI", cntr, 0);
            Roi ijRoi = rois[i];

            if (ijRoi.isDrawingTool()){//Checks if the given roi is a Text box/Arrow/Rounded Rectangle
                if (ijRoi.getTypeAsString().matches("Text")){
                    if (ijRoi instanceof TextRoi){
                        store.setLabelID(polylineID, cntr, 0);
                        storeText((TextRoi) ijRoi, store, cntr, 0);
                    }

                }
                else if (ijRoi.getTypeAsString().matches("Rectangle")){
                    if (ijRoi instanceof Roi){
                        store.setRectangleID(polylineID, cntr, 0);
                        storeRectangle(ijRoi, store, cntr, 0);
                    }
                }
                else {
                    roiID = null;
                    String type = ijRoi.getName();
                    IJ.log("ROI ID : " + type + " ROI type : " +  "Arrow (Drawing Tool) is not supported");
                }
            }
            else if (ijRoi instanceof OvalRoi) {//Check if its an oval or ellipse ROI
                store.setEllipseID(polylineID, cntr, 0);
                storeOval((OvalRoi) ijRoi, store, cntr, 0);
            }


            else if (ijRoi instanceof Line) { //Check if its a Line or Arrow ROI
                boolean checkpoint = ijRoi.isDrawingTool();
                if (!checkpoint){
                    store.setLineID(polylineID, cntr, 0);
                    storeLine((Line) ijRoi, store, cntr, 0);
                }
                else {
                    roiID = null;
                    String type = ijRoi.getName();
                    IJ.log("ROI ID : " + type + " ROI type : " +  "Arrow (Drawing Tool) is not supported");
                }
            }
            else if (ijRoi instanceof PolygonRoi || ijRoi instanceof EllipseRoi) {
                if (ijRoi.getTypeAsString().matches("Polyline") || ijRoi.getTypeAsString().matches("Freeline") || ijRoi.getTypeAsString().matches("Angle")){
                    store.setPolylineID(polylineID, cntr, 0);
                    storePolygon((PolygonRoi) ijRoi, store, cntr, 0);
                }
                else if (ijRoi.getTypeAsString().matches("Point")){
                    store.setPointID(polylineID, cntr, 0);
                    storePoint((PointRoi) ijRoi, store, cntr, 0);
                }
                else if (ijRoi.getTypeAsString().matches("Polygon") || ijRoi.getTypeAsString().matches("Freehand") || ijRoi.getTypeAsString().matches("Traced") || ijRoi.getTypeAsString().matches("Oval")){
                    store.setPolygonID(polylineID, cntr, 0);
                    storePolygon((PolygonRoi) ijRoi, store, cntr, 0);
                }
            }
            else if (ijRoi instanceof ShapeRoi) {
                Roi[] subRois = ((ShapeRoi) ijRoi).getRois();
                for (int q=0; q<subRois.length; q++) {

                    polylineID = MetadataTools.createLSID("Shape", cntr, q);
                    roiID = MetadataTools.createLSID("ROI", cntr, q);
                    Roi ijShape = subRois[q];

                    if (ijShape.isDrawingTool()){//Checks if the given roi is a Text box/Arrow/Rounded Rectangle
                        if (ijShape.getTypeAsString().matches("Text")){
                            if (ijShape instanceof TextRoi){
                                store.setLabelID(polylineID, cntr, q);
                                storeText((TextRoi) ijShape, store, cntr, q);
                            }
                        }
                        else if (ijShape.getTypeAsString().matches("Rectangle")){
                            if (ijShape instanceof Roi){
                                store.setRectangleID(polylineID, cntr, q);
                                storeRectangle(ijShape, store, cntr, q);
                            }
                        }
                        else {
                            roiID = null;
                            String type = ijShape.getName();
                            IJ.log("ROI ID : " + type + " ROI type : " +  "Arrow (Drawing Tool) is not supported");
                        }
                    }

                    else if (ijShape instanceof Line) {
                        boolean checkpoint = ijShape.isDrawingTool();
                        if (!checkpoint){
                            store.setLineID(polylineID, cntr, 0);
                            storeLine((Line) ijShape, store, cntr, 0);
                        }
                        else {
                            roiID = null;
                            String type1 = ijShape.getName();
                            discardList.add(type1);
                            IJ.log("ROI ID : " + type1 + " ROI type : " + "Arrow (DrawingTool) is not supported");
                        }
                    }
                    else if (ijShape instanceof OvalRoi) {
                        store.setEllipseID(polylineID, cntr, q);
                        storeOval((OvalRoi) ijShape, store, cntr, q);
                    }
                    else if (ijShape instanceof PolygonRoi || ijShape instanceof EllipseRoi) {
                        if (ijShape.getTypeAsString().matches("Polyline") || ijShape.getTypeAsString().matches("Freeline") || ijShape.getTypeAsString().matches("Angle")){
                            store.setPolylineID(polylineID, cntr, q);
                            storePolygon((PolygonRoi) ijShape, store, cntr, q);
                        }
                        else if (ijShape.getTypeAsString().matches("Point")){
                            store.setPointID(polylineID, cntr, q);
                            storePoint((PointRoi) ijShape, store, cntr, q);
                        }
                        else if (ijShape.getTypeAsString().matches("Polygon") || ijShape.getTypeAsString().matches("Freehand") || ijShape.getTypeAsString().matches("Traced") || ijShape.getTypeAsString().matches("Oval")){

                            store.setPolygonID(polylineID, cntr, q);
                            storePolygon((PolygonRoi) ijShape, store, cntr, q);
                        }
                    }
                    else if (ijShape.getTypeAsString().matches("Rectangle")){
                        store.setRectangleID(polylineID, cntr, q);
                        storeRectangle(ijShape, store, cntr, q);
                    }
                    else {
                        roiID = null;
                        String type = ijShape.getName();
                        IJ.log("ROI ID : " + type + " ROI type : " + ijShape.getTypeAsString() + "is not supported");
                    }
                }
            }

            else if(ijRoi.getTypeAsString().matches("Rectangle")){//Check if its a Rectangle or Rounded Rectangle ROI
                store.setRectangleID(polylineID, cntr, 0);
                storeRectangle(ijRoi, store, cntr, 0);
            }
            else {

                roiID = null;
                String type = ijRoi.getName();
                IJ.log("ROI ID : " + type + " ROI type : " + rois[cntr].getTypeAsString() + "is not supported");

            }

            //Save Roi's using ROIHandler
            if (roiID != null) {
                store.setROIID(roiID, cntr);
                store.setImageROIRef(roiID, 0, cntr);
                cntr++;
            }
        }
    }

    // -- Helper methods --

    private static void storeText(TextRoi roi, MetadataStore store, int roiNum, int shape) {

        store.setLabelX(roi.getPolygon().getBounds().getX(), roiNum, shape);
        store.setLabelY(roi.getPolygon().getBounds().getY(), roiNum, shape);

        store.setLabelText(roi.getText().trim(), roiNum, shape);
        store.setLabelFontSize(new Length(roi.getCurrentFont().getSize(), UNITS.PIXEL), roiNum, shape);

        if (roi.getStrokeWidth() > 0) {
            store.setLabelStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
        }
        if (roi.getStrokeColor() != null) {
            store.setLabelStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
        }
        if (roi.getFillColor() != null){
            store.setLabelFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
        }

    }

    private static void storePoint(PointRoi roi, MetadataStore store,
            int roiNum, int shape) {

        int[] xCoordinates = roi.getPolygon().xpoints;
        int[] yCoordinates = roi.getPolygon().ypoints;

        for (int cntr=0 ; cntr<xCoordinates.length; cntr++){
            String polylineID = MetadataTools.createLSID("Shape", roiNum, shape+cntr);
            store.setPointID(polylineID, roiNum, shape+cntr);
            store.setPointX((double) xCoordinates[cntr], roiNum, shape+cntr);
            store.setPointY((double) yCoordinates[cntr], roiNum, shape+cntr);
            store.setPointText(roi.getName(), roiNum, shape+cntr);
            if (roi.getStrokeWidth() > 0) {
                store.setPointStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape+cntr);
            }
            if (roi.getStrokeColor() != null) {
                store.setPointStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape+cntr);
            }
            if (roi.getFillColor() != null){
                store.setPointFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape+cntr);
            }

        }

    }

    /** Store a Line ROI in the given MetadataStore. */
    private static void storeLine(Line roi, MetadataStore store,
            int roiNum, int shape)
    {
        store.setLineX1(new Double(roi.x1), roiNum, shape);
        store.setLineX2(new Double(roi.x2), roiNum, shape);
        store.setLineY1(new Double(roi.y1), roiNum, shape);
        store.setLineY2(new Double(roi.y2), roiNum, shape);

        store.setLineText(roi.getName(), roiNum, shape);
        if (roi.getStrokeWidth() > 0) {
            store.setLineStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
        }
        if (roi.getStrokeColor() != null) {
            store.setLineStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
        }
        if (roi.getFillColor() != null){
            store.setLineFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
        }

    }

    /** Store an Roi (rectangle) in the given MetadataStore. */
    private static void storeRectangle(Roi roi, MetadataStore store,
            int roiNum, int shape)
    {
        Rectangle bounds = roi.getBounds();
        store.setRectangleX(new Double(bounds.x), roiNum, shape);
        store.setRectangleY(new Double(bounds.y), roiNum, shape);
        store.setRectangleWidth(new Double(bounds.width), roiNum, shape);
        store.setRectangleHeight(new Double(bounds.height), roiNum, shape);

        store.setRectangleText(roi.getName(), roiNum, shape);
        if (roi.getStrokeWidth() > 0) {
            store.setRectangleStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
        }
        if (roi.getStrokeColor() != null) {
            store.setRectangleStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
        }
        if (roi.getFillColor() != null){
            store.setRectangleFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
        }

    }

    /** Store a Polygon ROI in the given MetadataStore. */
    private static void storePolygon(PolygonRoi roi, MetadataStore store,
            int roiNum, int shape)
    {
        //        Rectangle bounds = roi.getBounds();
        //        int[] xCoordinates = roi.getXCoordinates();
        //        int[] yCoordinates = roi.getYCoordinates();
        //        StringBuffer points = new StringBuffer();
        //        for (int i=0; i<xCoordinates.length; i++) {
        //            points.append(xCoordinates[i] + bounds.x);
        //            points.append(",");
        //            points.append(yCoordinates[i] + bounds.y);
        //            if (i < xCoordinates.length - 1) points.append(" ");
        //        }

        int[] xCoordinates = roi.getPolygon().xpoints;
        int[] yCoordinates = roi.getPolygon().ypoints;
        String st1 = roi.getTypeAsString();
        String points = "1";
        for (int i=0 ; i<xCoordinates.length ; i++){

            if(i==0){                    
                points = (xCoordinates[i] + "," + yCoordinates[i]);
            }else{
                points= (points + " " + xCoordinates[i] + "," + yCoordinates[i]);
            }
        }

        if (st1.matches("Polyline") || st1.matches("Freeline") || st1.matches("Angle")) {
            store.setPolylinePoints(points.toString(), roiNum, shape);
            store.setPolylineText(roi.getName(), roiNum, shape);
            if (roi.getStrokeWidth() > 0) {
                store.setPolylineStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
            }
            if (roi.getStrokeColor() != null) {
                store.setPolylineStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
            }
            if (roi.getFillColor() != null){
                store.setPolylineFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
            }
        }
        else if (st1.matches("Polygon") || st1.matches("Freehand") || st1.matches("Traced")){
            store.setPolygonPoints(points.toString(), roiNum, shape);
            store.setPolygonText(roi.getName(), roiNum, shape);
            if (roi.getStrokeWidth() > 0) {
                store.setPolygonStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
            }
            if (roi.getStrokeColor() != null) {
                store.setPolygonStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
            }
            if (roi.getFillColor() != null){
                store.setPolygonFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
            }
        }

    }

    /** Store an Oval ROI in the given MetadataStore. */
    private static void storeOval(OvalRoi roi,
            MetadataStore store, int roiNum, int shape)
    {
        Rectangle vnRectBounds = roi.getPolygon().getBounds();
        int x = vnRectBounds.x;
        int y = vnRectBounds.y;

        double rx = vnRectBounds.getWidth();
        double ry = vnRectBounds.getHeight();

        store.setEllipseX(((double) x + rx/2), roiNum, shape);
        store.setEllipseY(((double) y + ry/2), roiNum, shape);
        store.setEllipseRadiusX((double) rx/2, roiNum, shape);
        store.setEllipseRadiusY((double) ry/2, roiNum, shape);
        store.setEllipseText(roi.getName(), roiNum, shape);
        if (roi.getStrokeWidth() > 0) {
            store.setEllipseStrokeWidth( new Length((roi.getStrokeWidth()), UNITS.PIXEL), roiNum, shape);
        }
        if (roi.getStrokeColor() != null) {
            store.setEllipseStrokeColor(toOMExmlColor(roi.getStrokeColor()) , roiNum, shape);
        }
        if (roi.getFillColor() != null){
            store.setEllipseFillColor(toOMExmlColor(roi.getFillColor()) , roiNum, shape);
        }

    }

    /**
     * Parse (x, y) coordinates from a String returned by
     * MetadataRetrieve.getPolygonpoints(...) or
     * MetadataRetrieve.getPolylinepoints(...)
     */
    private static int[][] parsePoints(String points) {
        // assuming points are stored like this:
        // x0,y0 x1,y1 x2,y2 ...
        String[] pointList = points.split(" ");
        int[][] coordinates = new int[2][pointList.length];

        for (int q=0; q<pointList.length; q++) {
            pointList[q] = pointList[q].trim();
            int delim = pointList[q].indexOf(",");
            coordinates[0][q] =
                    (int) Double.parseDouble(pointList[q].substring(0, delim));
            coordinates[1][q] =
                    (int) Double.parseDouble(pointList[q].substring(delim + 1));
        }
        return coordinates;
    }

    private static ome.xml.model.primitives.Color toOMExmlColor(java.awt.Color color){

        ome.xml.model.primitives.Color test = new ome.xml.model.primitives.Color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
        return test ;

    }

}
