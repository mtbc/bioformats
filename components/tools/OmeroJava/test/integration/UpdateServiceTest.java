/*
 * $Id$
 *
 *   Copyright 2006-2010 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package integration;

import static omero.rtypes.rstring;
import static omero.rtypes.rtime;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import omero.api.IPixelsPrx;
import omero.model.Annotation;
import omero.model.AnnotationAnnotationLinkI;
import omero.model.BooleanAnnotation;
import omero.model.BooleanAnnotationI;
import omero.model.Channel;
import omero.model.ColorI;
import omero.model.CommentAnnotation;
import omero.model.CommentAnnotationI;
import omero.model.Dataset;
import omero.model.DatasetAnnotationLink;
import omero.model.DatasetAnnotationLinkI;
import omero.model.DatasetI;
import omero.model.DatasetImageLink;
import omero.model.DatasetImageLinkI;
import omero.model.Detector;
import omero.model.Dichroic;
import omero.model.EllipseI;
import omero.model.FileAnnotation;
import omero.model.FileAnnotationI;
import omero.model.FillRule;
import omero.model.Filter;
import omero.model.FontFamily;
import omero.model.FontStyle;
import omero.model.IObject;
import omero.model.Image;
import omero.model.ImageAnnotationLink;
import omero.model.ImageAnnotationLinkI;
import omero.model.ImageI;
import omero.model.Instrument;
import omero.model.Laser;
import omero.model.Line;
import omero.model.LineCap;
import omero.model.LineI;
import omero.model.LongAnnotation;
import omero.model.LongAnnotationI;
import omero.model.Marker;
import omero.model.Mask;
import omero.model.MaskI;
import omero.model.Objective;
import omero.model.OriginalFile;
import omero.model.Pixels;
import omero.model.Plane;
import omero.model.Plate;
import omero.model.PlateAcquisition;
import omero.model.PlateAcquisitionAnnotationLink;
import omero.model.PlateAcquisitionAnnotationLinkI;
import omero.model.PlateAcquisitionI;
import omero.model.PlateAnnotationLink;
import omero.model.PlateAnnotationLinkI;
import omero.model.PlateI;
import omero.model.Point;
import omero.model.PointI;
import omero.model.Polygon;
import omero.model.PolygonI;
import omero.model.Polyline;
import omero.model.PolylineI;
import omero.model.Project;
import omero.model.ProjectAnnotationLink;
import omero.model.ProjectAnnotationLinkI;
import omero.model.ProjectDatasetLink;
import omero.model.ProjectDatasetLinkI;
import omero.model.ProjectI;
import omero.model.Reagent;
import omero.model.Rectangle;
import omero.model.RectangleI;
import omero.model.ROI;
import omero.model.ROII;
import omero.model.Screen;
import omero.model.ScreenAnnotationLink;
import omero.model.ScreenAnnotationLinkI;
import omero.model.ScreenI;
import omero.model.ScreenPlateLink;
import omero.model.TagAnnotation;
import omero.model.TagAnnotationI;
import omero.model.TermAnnotation;
import omero.model.TermAnnotationI;
import omero.sys.Parameters;
import omero.sys.ParametersI;

import org.testng.Assert;
import org.testng.annotations.Test;

import pojos.BooleanAnnotationData;
import pojos.DatasetData;
import pojos.EllipseData;
import pojos.ImageData;
import pojos.LineData;
import pojos.LongAnnotationData;
import pojos.MaskData;
import pojos.PlateAcquisitionData;
import pojos.PlateData;
import pojos.PointData;
import pojos.PolygonData;
import pojos.PolylineData;
import pojos.ProjectData;
import pojos.ROIData;
import pojos.RectangleData;
import pojos.ScreenData;
import pojos.ShapeData;
import pojos.ShapeSettingsData;
import pojos.TagAnnotationData;
import pojos.TermAnnotationData;
import pojos.TextualAnnotationData;

/** 
 * Collections of tests for the <code>IUpdate</code> service.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Josh Moore &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:josh.moore@gmx.de">josh.moore@gmx.de</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
@Test(groups = { "client", "integration", "blitz" })
public class UpdateServiceTest 
	extends AbstractServerTest
{
	
    /**
     * Test to create an image and make sure the version is correct.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "versions"})
    public void testVersionHandling() 
    	throws Exception
    {
        Image originalImage = createImage(1, 1, 1, 1, 1, "version handling");
        long originalVersion = originalImage.getDetails().getUpdateEvent().getId().getValue();
        
        originalImage.setDescription(rstring("version handling update"));
        // Update event should be created
        Image updatedImage = (Image) iUpdate.saveAndReturnObject(originalImage);
        long updatedVersion = updatedImage.getDetails().getUpdateEvent().getId().getValue();
        assert originalVersion != updatedVersion;
    }
    
    /**
     * Test to make sure that an update event is created for an object
     * after updating an annotation linked to the image.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "versions", "ticket:118" })
    public void testVersionNotIncreasingAfterUpdate()
            throws Exception 
    {
        CommentAnnotation ann = new CommentAnnotationI();
        Image img = createImageWithName("version_test");
        
        ann.setTextValue(rstring("version_test"));
        img.linkAnnotation(ann);

        img = (Image) iUpdate.saveAndReturnObject(img);
        ann = (CommentAnnotation) img.linkedAnnotationList().get(0);
        assertNotNull(img.getId());
        assertNotNull(ann.getId());
        long oldId = img.getDetails().getUpdateEvent().getId().getValue();
        ann.setTextValue(rstring("updated version_test"));
        ann = (CommentAnnotation) iUpdate.saveAndReturnObject(ann);
        img = (Image) iQuery.get(Image.class.getName(), img.getId().getValue()); 

        long newId = img.getDetails().getUpdateEvent().getId().getValue();
        assertTrue(newId == oldId);
    }
    
    /**
     * Test to make sure that an update event is not created when
     * when invoking the <code>SaveAndReturnObject</code> on an unmodified 
     * Object.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "versions", "ticket:118" })
    public void testVersionNotIncreasingOnUnmodifiedObject() 
    	throws Exception 
    {
        Image img = createBasicImage();
        
        assertNotNull(img.getDetails().getUpdateEvent());
        long id = img.getDetails().getUpdateEvent().getId().getValue();
        Image test = (Image) iUpdate.saveAndReturnObject(img);
        assertNotNull(test.getDetails().getUpdateEvent());
        assertTrue(id == test.getDetails().getUpdateEvent().getId().getValue());
    }

    /**
     * Tests the creation of a project without datasets.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:1106")
    public void testEmptyProject() 
    	throws Exception
    {
        Project p = (Project) iUpdate.saveAndReturnObject(
        		mmFactory.simpleProjectData().asIObject());
        assertNotNull(p);
        ProjectData pd = new ProjectData(p);
    	assertTrue(p.getId().getValue() > 0);
    	assertTrue(p.getId().getValue() == pd.getId());
    	assertTrue(p.getName().getValue() == pd.getName());
    	assertTrue(p.getDescription().getValue() == pd.getDescription());
    }
    
    /**
     * Tests the creation of a dataset.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:1106")
    public void testEmptyDataset() 
    	throws Exception
    {
        Dataset p = (Dataset) iUpdate.saveAndReturnObject(
        		mmFactory.simpleDatasetData().asIObject());
        assertNotNull(p);
        DatasetData d = new DatasetData(p);
    	assertTrue(p.getId().getValue() > 0);
    	assertTrue(p.getId().getValue() == d.getId());
    	assertTrue(p.getName().getValue() == d.getName());
    	assertTrue(p.getDescription().getValue() == d.getDescription());
    }
    
    /**
     * Tests the creation of an empty image and image data.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:1106")
    public void testEmptyImage() 
    	throws Exception
    {
        Image image = createBasicImage();
        ImageData imageData = new ImageData(image);
    	
    	assertTrue(image.getId().getValue() > 0);
    	assertEquals(image.getId().getValue(),imageData.getId());
    	assertEquals(image.getName().getValue(),imageData.getName());
    	assertEquals(image.getDescription().getValue(),imageData.getDescription());
    }
    
    /**
     * Tests the creation of an image with a set of pixels.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateImageWithPixels()
    	throws Exception 
    {
    	Image sourceImage = createBasicImage();
    	assertNotNull(sourceImage);
    	
    	ParametersI param = new ParametersI();
    	param.addId(sourceImage.getId().getValue());

    	StringBuilder sb = new StringBuilder();
    	sb.append("select i from Image i ");
    	sb.append("left outer join fetch i.pixels as pix ");
        sb.append("left outer join fetch pix.type as pt ");
    	sb.append("where i.id = :id");
    	
    	Image returnedImage = (Image) iQuery.findByQuery(sb.toString(), param);
    	assertNotNull(returnedImage);
    	
    	//Make sure we have a pixels set.
    	Pixels pixels = returnedImage.getPixels();
    	assertNotNull(pixels);
    }
    
    /**
     * Tests the creation of a screen.
     * 
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:1106")
    public void testEmptyScreen() 
    	throws Exception
    {
        Screen p = (Screen) 
        	factory.getUpdateService().saveAndReturnObject(
        			mmFactory.simpleScreenData().asIObject());
        ScreenData data = new ScreenData(p);
    	assertNotNull(p);
    	assertTrue(p.getId().getValue() > 0);
    	assertTrue(p.getId().getValue() == data.getId());
    	assertTrue(p.getName().getValue() == data.getName());
    	assertTrue(p.getDescription().getValue() == data.getDescription());
    }
    
    /**
     * Tests the creation of a screen.
     * 
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testEmptyPlate() 
    	throws Exception
    {
        Plate p = (Plate) 
        	factory.getUpdateService().saveAndReturnObject(
        			mmFactory.simplePlateData().asIObject());
        PlateData data = new PlateData(p);
    	assertNotNull(p);
    	assertTrue(p.getId().getValue() > 0);
    	assertTrue(p.getId().getValue() == data.getId());
    	assertTrue(p.getName().getValue() == data.getName());
    	assertTrue(p.getDescription().getValue() == data.getDescription());
    }
    
    /**
     * Tests the creation of a plate with wells, wells sample and 
     * plate acquisition.
     * 
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testPopulatedPlate()
		throws Exception
    {
    	Plate p = mmFactory.createPlate(1, 1, 1, 1, false);
    	p = savePlate(iUpdate, p, false);
    	
    	assertNotNull(p);
    	assertNotNull(p.getName().getValue());
    	assertNotNull(p.getStatus().getValue());
    	assertNotNull(p.getDescription().getValue());
    	assertNotNull(p.getExternalIdentifier().getValue());
    	
    	String sql = "select l from PlateAcquisition as l ";
    	sql += "join fetch l.plate as p ";
    	sql += "where p.id = :id";
    	
    	ParametersI param = new ParametersI();
    	param.addId(p.getId());
    	assertNotNull(iQuery.findByQuery(sql, param));
    	
    	p = mmFactory.createPlate(1, 1, 1, 0, false);
    	p = savePlate(iUpdate, p, false);
    	assertNotNull(p);
    	
    	p = mmFactory.createPlate(1, 1, 1, 1, true);
    	p = savePlate(iUpdate, p, false);
    	assertNotNull(p);
    }
    
    /**
     * Test to create a project and link datasets to it.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateProjectAndLinkDatasets() 
    	throws Exception 
    {
        String name = " 2&1 " + System.currentTimeMillis();
        Project p = new ProjectI();
        p.setName(rstring(name));

        p = (Project) iUpdate.saveAndReturnObject(p);

        Dataset d1 = new DatasetI();
        d1.setName(rstring(name));
        d1 = (Dataset) iUpdate.saveAndReturnObject(d1);

        Dataset d2 = new DatasetI();
        d2.setName(rstring(name));
        d2 = (Dataset) iUpdate.saveAndReturnObject(d2);

        List<IObject> links = new ArrayList<IObject>();
        ProjectDatasetLink link = new ProjectDatasetLinkI();
        link.setParent(p);
        link.setChild(d1);
        links.add(link);
        link = new ProjectDatasetLinkI();
        link.setParent(p);
        link.setChild(d2);
        links.add(link);
        //links dataset and project.
        iUpdate.saveAndReturnArray(links);
        
        //load the project
        ParametersI param = new ParametersI();
        param.addId(p.getId());
       
        StringBuilder sb = new StringBuilder();
        sb.append("select p from Project p ");
        sb.append("left outer join fetch p.datasetLinks pdl ");
        sb.append("left outer join fetch pdl.child ds ");
        sb.append("where p.id = :id");
        p = (Project) iQuery.findByQuery(sb.toString(), param);
        
        //Check the conversion of Project to ProjectData
        ProjectData pData = new ProjectData(p);
        Set<DatasetData> datasets = pData.getDatasets();
        //We should have 2 datasets
        assertTrue(datasets.size() == 2);
        int count = 0;
        Iterator<DatasetData> i = datasets.iterator();
        DatasetData dataset;
        while (i.hasNext()) {
        	dataset = i.next();
			if (dataset.getId() == d1.getId().getValue() ||
					dataset.getId() == d2.getId().getValue()) count++;
		}
        assertTrue(count == 2);
    }
    
    /**
     * Test to create a dataset and link images to it.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateDatasetAndLinkImages() 
    	throws Exception 
    {
        String name = " 2&1 " + System.currentTimeMillis();
        Dataset p = new DatasetI();
        p.setName(rstring(name));

        p = (Dataset) iUpdate.saveAndReturnObject(p);

        Image d1 = createImageWithName(name);
        Image d2 = createImageWithName(name);

        List<IObject> links = new ArrayList<IObject>();
        DatasetImageLink link = new DatasetImageLinkI();
        link.setParent(p);
        link.setChild(d1);
        links.add(link);
        link = new DatasetImageLinkI();
        link.setParent(p);
        link.setChild(d2);
        links.add(link);
        //links dataset and project.
        iUpdate.saveAndReturnArray(links);
        
        //load the project
        ParametersI param = new ParametersI();
        param.addId(p.getId());
       
        StringBuilder sb = new StringBuilder();
        sb.append("select p from Dataset p ");
        sb.append("left outer join fetch p.imageLinks pdl ");
        sb.append("left outer join fetch pdl.child ds ");
        sb.append("where p.id = :id");
        p = (Dataset) iQuery.findByQuery(sb.toString(), param);
        
        //Check the conversion of Project to ProjectData
        DatasetData pData = new DatasetData(p);
        Set<ImageData> images = pData.getImages();
        //We should have 2 datasets
        assertTrue(images.size() == 2);
        int count = 0;
        Iterator<ImageData> i = images.iterator();
        ImageData image;
        while (i.hasNext()) {
        	image = i.next();
			if (image.getId() == d1.getId().getValue() ||
					image.getId() == d2.getId().getValue()) count++;
		}
        assertTrue(count == 2);
    }

    //Annotation section
    
    /**
     * Links the passed annotation and test if correctly linked.
     * @throws Exception Thrown if an error occurred.
     */
    private void linkAnnotationAndObjects(Annotation data)
    	throws Exception 
    {
    	//Image
        Image i = createBasicImage();
        
        ImageAnnotationLink l = new ImageAnnotationLinkI();
        l.setParent((Image) i.proxy());
        l.setChild((Annotation) data.proxy());
        IObject o1 = iUpdate.saveAndReturnObject(l);
        assertNotNull(o1);
        l  = (ImageAnnotationLink) o1;
        assertTrue(l.getChild().getId().getValue() == data.getId().getValue());
        assertTrue(l.getParent().getId().getValue() == i.getId().getValue());
        
        //Project
        Project p = (Project) iUpdate.saveAndReturnObject(
        		mmFactory.simpleProjectData().asIObject());
        ProjectAnnotationLink pl = new ProjectAnnotationLinkI();
        pl.setParent((Project) p.proxy());
        pl.setChild((Annotation) data.proxy());
        o1 = iUpdate.saveAndReturnObject(pl);
        assertNotNull(o1);
        pl  = (ProjectAnnotationLink) o1;
        assertTrue(pl.getChild().getId().getValue() == data.getId().getValue());
        assertTrue(pl.getParent().getId().getValue() == p.getId().getValue());
        
        //Dataset
        Dataset d = (Dataset) iUpdate.saveAndReturnObject(
        		mmFactory.simpleDatasetData().asIObject());
        DatasetAnnotationLink dl = new DatasetAnnotationLinkI();
        dl.setParent((Dataset) d.proxy());
        dl.setChild((Annotation) data.proxy());
        o1 = iUpdate.saveAndReturnObject(dl);
        assertNotNull(o1);
        dl  = (DatasetAnnotationLink) o1;
        assertTrue(dl.getChild().getId().getValue() == data.getId().getValue());
        assertTrue(dl.getParent().getId().getValue() == d.getId().getValue());
        
        //Screen
        Screen s = (Screen) iUpdate.saveAndReturnObject(
        		mmFactory.simpleScreenData().asIObject());
        ScreenAnnotationLink sl = new ScreenAnnotationLinkI();
        sl.setParent((Screen) s.proxy());
        sl.setChild((Annotation) data.proxy());
        o1 = iUpdate.saveAndReturnObject(sl);
        assertNotNull(o1);
        sl  = (ScreenAnnotationLink) o1;
        assertTrue(sl.getChild().getId().getValue() == data.getId().getValue());
        assertTrue(sl.getParent().getId().getValue() == s.getId().getValue());
        
        //Plate
        Plate pp = (Plate) iUpdate.saveAndReturnObject(
        		mmFactory.simplePlateData().asIObject());
        PlateAnnotationLink ppl = new PlateAnnotationLinkI();
        ppl.setParent((Plate) pp.proxy());
        ppl.setChild((Annotation) data.proxy());
        o1 = iUpdate.saveAndReturnObject(ppl);
        assertNotNull(o1);
        ppl  = (PlateAnnotationLink) o1;
        assertTrue(ppl.getChild().getId().getValue() == data.getId().getValue());
        assertTrue(ppl.getParent().getId().getValue() == pp.getId().getValue());
    }
    
    /**
     * Tests to create a comment annotation and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateCommentAnnotation()
    	throws Exception 
    {
    	CommentAnnotation annotation = new CommentAnnotationI();
    	annotation.setTextValue(omero.rtypes.rstring("comment"));
    	annotation = (CommentAnnotation) 
    	iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(annotation);
    	linkAnnotationAndObjects(annotation);
    	TextualAnnotationData data = new TextualAnnotationData(annotation);
    	assertNotNull(data);
    	assertTrue(data.getText().equals(annotation.getTextValue().getValue()));
    }
    
    /**
     * Tests to create a tag annotation and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateTagAnnotation()
    	throws Exception 
    {
    	TagAnnotation annotation = new TagAnnotationI();
    	annotation.setTextValue(omero.rtypes.rstring("tag"));
    	annotation = (TagAnnotation) 
    		iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(annotation);
    	linkAnnotationAndObjects(annotation);
    	TagAnnotationData data = new TagAnnotationData(annotation);
    	assertNotNull(data);
    	assertTrue(data.getTagValue().equals(
    			annotation.getTextValue().getValue()));
    }
    
    /**
     * Tests to create a boolean annotation and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateBooleanAnnotation()
    	throws Exception 
    {
    	BooleanAnnotation annotation = new BooleanAnnotationI();
    	annotation.setBoolValue(omero.rtypes.rbool(true));
    	annotation = (BooleanAnnotation) 
    		iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(annotation);
    	linkAnnotationAndObjects(annotation);
    	BooleanAnnotationData data = new BooleanAnnotationData(annotation);
    	assertNotNull(data);
    }
    
    /**
     * Tests to create a long annotation and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateLongAnnotation()
    	throws Exception 
    {
    	LongAnnotation annotation = new LongAnnotationI();
    	annotation.setLongValue(omero.rtypes.rlong(1L));
    	annotation = (LongAnnotation) 
    		iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(annotation);
    	linkAnnotationAndObjects(annotation);
    	LongAnnotationData data = new LongAnnotationData(annotation);
    	assertNotNull(data);
    	assertTrue(data.getDataValue() == annotation.getLongValue().getValue());
    }
    
    /**
     * Tests to create a file annotation and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateFileAnnotation()
    	throws Exception 
    {
    	OriginalFile of = (OriginalFile) iUpdate.saveAndReturnObject(
    			mmFactory.createOriginalFile());
		assertNotNull(of);
		FileAnnotation fa = new FileAnnotationI();
		fa.setFile(of);
		FileAnnotation data = (FileAnnotation) iUpdate.saveAndReturnObject(fa);
		assertNotNull(data);
    	linkAnnotationAndObjects(data);
    }
    
    /**
     * Tests to create a term and link it to various objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testCreateTermAnnotation()
    	throws Exception 
    {
		TermAnnotation term = new TermAnnotationI();
		term.setTermValue(omero.rtypes.rstring("term"));
		term = (TermAnnotation) iUpdate.saveAndReturnObject(term);
		assertNotNull(term);
    	linkAnnotationAndObjects(term);
    	TermAnnotationData data = new TermAnnotationData(term);
    	assertNotNull(data);
    	assertTrue(data.getTerm().equals(term.getTermValue().getValue()));
    }
    
    /**
     * Tests to unlink of an annotation. Creates only one type of annotation.
     * This method uses the <code>deleteObject</code> method.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testRemoveAnnotation()
    	throws Exception 
    {
    	LongAnnotationI annotation = new LongAnnotationI();
    	annotation.setLongValue(omero.rtypes.rlong(1L));
    	LongAnnotation data = (LongAnnotation) 
    		iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(data);
    	//Image
        Image image = createBasicImage();
        ImageAnnotationLink link = new ImageAnnotationLinkI();
        link.setParent((Image) image.proxy());
        link.setChild((Annotation) data.proxy());
        link = (ImageAnnotationLink) iUpdate.saveAndReturnObject(link);
        assertNotNull(link);
        long id = link.getId().getValue();
        //annotation and image are linked. Remove the link.
        iUpdate.deleteObject(link);
        //now check that the image is no longer linked to the annotation
        String sql = "select link from ImageAnnotationLink as link";
		sql += " where link.id = :id";
		ParametersI p = new ParametersI();
		p.addId(id);
		IObject object = iQuery.findByQuery(sql, p);
		assertNull(object);
    }
    
    /**
     * Tests to update an annotation.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testUpdateAnnotation() 
    	throws Exception 
    {
    	CommentAnnotationI annotation = new CommentAnnotationI();
    	annotation.setTextValue(omero.rtypes.rstring("comment"));
    	CommentAnnotation data = (CommentAnnotation) 
    		iUpdate.saveAndReturnObject(annotation);
    	assertNotNull(data);
    	//modified the text
    	String newText = "commentModified";
    	data.setTextValue(omero.rtypes.rstring(newText));
    	CommentAnnotation update = (CommentAnnotation) 
			iUpdate.saveAndReturnObject(data);
    	assertNotNull(update);
    	
    	assertTrue(data.getId().getValue() == update.getId().getValue());
    	
    	assertTrue(newText.equals(update.getTextValue().getValue()));
    }
    
    /**
     * Tests the creation of tag annotation, linked it to an image by a
     * user and link it to the same image by a different user.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testUpdateSameTagAnnotationUsedByTwoUsers() 
    	throws Exception
    {
        String groupName = newUserAndGroup("rwrw--").groupName;

        //create an image.
        Image image = createBasicImage();

        //create the tag.
        TagAnnotationI tag = new TagAnnotationI();
        tag.setTextValue(omero.rtypes.rstring("tag1"));

        Annotation data = (Annotation) iUpdate.saveAndReturnObject(tag);
        //link the image and the tag
        ImageAnnotationLink l = new ImageAnnotationLinkI();
        l.setParent((Image) image.proxy());
        l.setChild((Annotation) data.proxy());

        IObject o1 = iUpdate.saveAndReturnObject(l);
        assertNotNull(o1);
        CreatePojosFixture2 fixture = CreatePojosFixture2.withNewUser(root, 
        		groupName);

        l = new ImageAnnotationLinkI();
        l.setParent((Image) image.proxy());
        l.setChild((Annotation) data.proxy());
        // l.getDetails().setOwner(fixture.e);
        IObject o2 = fixture.iUpdate.saveAndReturnObject(l);
        assertNotNull(o2);

        long self = factory.getAdminService().getEventContext().userId;

        assertTrue(o1.getId().getValue() != o2.getId().getValue());
        assertTrue(o1.getDetails().getOwner().getId().getValue() == self);
        assertTrue(o2.getDetails().getOwner().getId().getValue() ==
            fixture.e.getId().getValue());
    }
    
    /**
     * Tests the creation of tag annotation, linked it to an image by a
     * user and link it to the same image by a different user.
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testTagSetTagCreation() 
    	throws Exception
    {
    	//Create a tag set.
    	TagAnnotationI tagSet = new TagAnnotationI();
    	tagSet.setTextValue(omero.rtypes.rstring("tagSet"));
    	tagSet.setNs(omero.rtypes.rstring(TagAnnotationData.INSIGHT_TAGSET_NS));
    	TagAnnotation tagSetReturned = 
    		(TagAnnotation) iUpdate.saveAndReturnObject(tagSet);
    	//create a tag and link it to the tag set
    	assertNotNull(tagSetReturned);
    	TagAnnotationI tag = new TagAnnotationI();
    	tag.setTextValue(omero.rtypes.rstring("tag"));
    	TagAnnotation tagReturned = 
    		(TagAnnotation) iUpdate.saveAndReturnObject(tag);
    	assertNotNull(tagReturned);
    	AnnotationAnnotationLinkI link = new AnnotationAnnotationLinkI();
    	link.setChild(tagReturned);
    	link.setParent(tagSetReturned);
    	IObject l = iUpdate.saveAndReturnObject(link); //save the link.
    	assertNotNull(l);

    	ParametersI param = new ParametersI();
    	param.addId(l.getId());

    	StringBuilder sb = new StringBuilder();
    	sb.append("select l from AnnotationAnnotationLink l ");
    	sb.append("left outer join fetch l.child c ");
    	sb.append("left outer join fetch l.parent p ");
    	sb.append("where l.id = :id");
    	AnnotationAnnotationLinkI lReturned = (AnnotationAnnotationLinkI) 
    	iQuery.findByQuery(sb.toString(), param);
    	assertNotNull(lReturned.getChild());
    	assertNotNull(lReturned.getParent());
    	assertTrue(lReturned.getChild().getId().getValue() 
    			== tagReturned.getId().getValue());
    	assertTrue(lReturned.getParent().getId().getValue() 
    			== tagSetReturned.getId().getValue());
    }

    //
    // The following are duplicated in ome.server.itests.update.UpdateTest
    //
    
    /**
     * Test the creation and handling of channels.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:2547")
    public void testChannelMoveWithFullArrayGoesToEnd() 
    	throws Exception
    {
    	int totalInitalChannels = ModelMockFactory.DEFAULT_CHANNELS_NUMBER;
    	Image image = createImage(1,1,1,1,totalInitalChannels, "testChannelMoveWithFullArrayGoesToEnd");
        Pixels pixels = image.getPixels();

        assertEquals(totalInitalChannels, pixels.sizeOfChannels());
        
        List<Channel> initialChannels = pixels.copyChannels();
        Set<Long> initalChannelIds = new HashSet<Long>();
        	
        for (Channel channel : initialChannels) {
            assertNotNull(channel);
            initalChannelIds.add(channel.getId().getValue());
        }

        // Now add another channel
        Channel extra = mmFactory.createChannel(0);
        image.getPixels().addChannel(extra);
        Pixels returnedPixels = (Pixels) iUpdate.saveAndReturnObject(image.getPixels());

        // assert we have 1 more channel
        assertEquals(totalInitalChannels + 1, returnedPixels.sizeOfChannels());
        
        // assert that the returnedChannels contains all the existing channels
        List<Channel> returnedChannels =  returnedPixels.copyChannels();
        Set<Long> returnedChannelIds = new HashSet<Long>();
        
        for(Channel returnedChannel : returnedChannels)
        {
        	returnedChannelIds.add(returnedChannel.getId().getValue());
        }

        for(Long initalChannelId : initalChannelIds)
        {
        	assert returnedChannelIds.contains(initalChannelId);
        }
    }

    /**
     * Test the creation and handling of channels.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:2547")
    public void testChannelMoveWithSpaceFillsSpace() 
    	throws Exception
    {
    	int totalChannels = ModelMockFactory.DEFAULT_CHANNELS_NUMBER;
    	Image image = createImage(1, 1, 1, 0, totalChannels, "testChannelMoveWithSpaceFillsSpace");
        
        Pixels pixels = image.getPixels();
        pixels.copyChannels().add(1, null);
        pixels = (Pixels) iUpdate.saveAndReturnObject(pixels);      

        Set<Long> ids = new HashSet<Long>();
        Channel old = pixels.copyChannels().get(0);
        assertEquals(totalChannels, pixels.sizeOfChannels());
        assertNotNull(old);
        ids.add(pixels.copyChannels().get(0).getId().getValue());

        // Middle should be empty
        assertNull(pixels.copyChannels().get(1));

        assertNotNull(pixels.copyChannels().get(2));
        ids.add(pixels.copyChannels().get(2).getId().getValue());

        // Now add a channel to the front
        
        //extra = (Channel) iUpdate.saveAndReturnObject(extra);
        //p.setChannel(0, extra);
        pixels.copyChannels().add(1, old);
        
        pixels = (Pixels) iUpdate.saveAndReturnObject(pixels);
        Channel extra =  mmFactory.createChannel(0);
        pixels.copyChannels().add(0, extra);
        
        pixels = (Pixels) iUpdate.saveAndReturnObject(pixels);

        assertEquals(ModelMockFactory.DEFAULT_CHANNELS_NUMBER, 
        		pixels.sizeOfChannels());
        assertFalse(ids.contains(pixels.copyChannels().get(0).getId().getValue()));
    }

    /**
     * Test the creation and handling of channels.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:2547")
    public void testChannelToSpaceChangesNothing() 
    	throws Exception
    {
    	int totalChannels = ModelMockFactory.DEFAULT_CHANNELS_NUMBER;
    	Image image = createImage(1, 1, 1, 0, totalChannels, "testChannelToSpaceChangesNothing");

        Pixels p = image.getPixels();
        p.copyChannels().add(1, null);
        p = (Pixels) iUpdate.saveAndReturnObject(p);

        Set<Long> ids = new HashSet<Long>();
        assertEquals(ModelMockFactory.DEFAULT_CHANNELS_NUMBER, 
        		p.sizeOfChannels());
        assertNotNull(p.copyChannels().get(0));
        ids.add(p.copyChannels().get(0).getId().getValue());

        // Middle should be empty
        assertNull(p.copyChannels().get(1));

        assertNotNull(p.copyChannels().get(2));
        ids.add(p.copyChannels().get(2).getId().getValue());

        // Now add a channel to the space
        Channel extra =  mmFactory.createChannel(0);
        p.copyChannels().add(1, extra);

        p = (Pixels) iUpdate.saveAndReturnObject(p);

        assertEquals(ModelMockFactory.DEFAULT_CHANNELS_NUMBER, 
        		p.sizeOfChannels());
        assertFalse(ids.contains(p.copyChannels().get(1).getId().getValue()));
    }
    
    /**
     * Tests the creation of plane information objects.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "ticket:168", "ticket:767" })
    public void testPlaneSetPixelsSavePlaneInfo() 
    	throws Exception
    {
    	Image image = createBasicImage();
        Pixels pixels = image.getPixels();
        pixels.clearPlanes();
        Plane planeInfo = mmFactory.createPlane();
        planeInfo.setPixels(pixels);
        planeInfo = (Plane) iUpdate.saveAndReturnObject(planeInfo);
        ParametersI param = new ParametersI();
    	param.addId(planeInfo.getId());
        Pixels test = (Pixels) iQuery.findByQuery(
                "select pi.pixels from Plane pi where pi.id = :id",
                param);
        assertNotNull(test);
    }

    /**
     * Tests the creation of plane information objects. This time the plane info
     * object is directly added to the pixels set. The plane info should be
     * saved.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = "ticket:168")
    public void testPixelsAddToPlaneSavePixels() 
    	throws Exception
    {
    	Image image = createBasicImage();
        Pixels pixels = image.getPixels();
    	pixels.clearPlanes();
    	Plane planeInfo = mmFactory.createPlane();
    	pixels.addPlane(planeInfo);
    	pixels = (Pixels) iUpdate.saveAndReturnObject(pixels);
    	ParametersI param = new ParametersI();
    	param.addId(pixels.getId());
    	List<IObject> test = (List<IObject>) iQuery.findAllByQuery(
    			"select pi from Plane pi where pi.pixels.id = :id",
    			param);
    	assertTrue(test.size() > 0);
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Ellipses and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithEllipse() 
    	throws Exception
    {
    	Image image = createBasicImage();
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROII) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        int z = 0;
        int t = 0;
        int c = 0;
        EllipseI rect = new EllipseI();
        rect.setX(omero.rtypes.rdouble(v));
        rect.setY(omero.rtypes.rdouble(v));
        rect.setRadiusX(omero.rtypes.rdouble(v));
        rect.setRadiusY(omero.rtypes.rdouble(v));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        EllipseData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (EllipseData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getX() == v);
        	assertTrue(shape.getY() == v);
        	assertTrue(shape.getRadiusX() == v);
        	assertTrue(shape.getRadiusY() == v);
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Points and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithPoint() 
    	throws Exception
    {
        Image image = createBasicImage();
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        int z = 0;
        int t = 0;
        int c = 0;
        Point rect = new PointI();
        rect.setX(omero.rtypes.rdouble(v));
        rect.setY(omero.rtypes.rdouble(v));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        PointData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (PointData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getX() == v);
        	assertTrue(shape.getY() == v);
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are rectangle and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithRectangle() 
    	throws Exception
    {
        Image image = createBasicImage();
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        int z = 0;
        int t = 0;
        int c = 0;
        Rectangle rect = new RectangleI();
        rect.setX(omero.rtypes.rdouble(v));
        rect.setY(omero.rtypes.rdouble(v));
        rect.setWidth(omero.rtypes.rdouble(v));
        rect.setHeight(omero.rtypes.rdouble(v));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        RectangleData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (RectangleData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getX() == v);
        	assertTrue(shape.getY() == v);
        	assertTrue(shape.getWidth() == v);
        	assertTrue(shape.getHeight() == v);
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Points and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateShapeSettings() 
    	throws Exception
    {
        Image image = createBasicImage();
        IPixelsPrx svc = factory.getPixelsService();
     	List<IObject> values = svc.getAllEnumerations(FillRule.class.getName());
     	FillRule rule = (FillRule) values.get(0);
     	values = svc.getAllEnumerations(FontFamily.class.getName());
     	FontFamily family = (FontFamily) values.get(0);
     	values = svc.getAllEnumerations(FontStyle.class.getName());
     	FontStyle style = (FontStyle) values.get(0);
     	values = svc.getAllEnumerations(LineCap.class.getName());
     	LineCap lineCap = (LineCap) values.get(0);
     	Color fillColor = Color.RED;
     	Color strokeColor = Color.GREEN;
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double strokeWidth = 2;
        double v = 10;
        int z = 0;
        int t = 0;
        int c = 0;
        Rectangle rect = new RectangleI();
        rect.setX(omero.rtypes.rdouble(v));
        rect.setY(omero.rtypes.rdouble(v));
        rect.setWidth(omero.rtypes.rdouble(v));
        rect.setHeight(omero.rtypes.rdouble(v));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        //Set the settings
        rect.setFillRule(rule);
        rect.setFontFamily(family);
        rect.setFontStyle(style);
        rect.setLineCap(lineCap);
        
        rect.setFillColor(new ColorI(fillColor.getRGB()));
        rect.setStrokeColor(new ColorI(strokeColor.getRGB()));
        rect.setStrokeWidth(omero.rtypes.rdouble(strokeWidth));
        
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        RectangleData shape;
        Iterator<ShapeData> i = shapes.iterator();
        ShapeSettingsData settings;
        Color color;
        while (i.hasNext()) {
        	shape = (RectangleData) i.next();
        	settings = shape.getShapeSettings();
        	assertEquals(settings.getFillRule(), rule.getValue().getValue());
        	assertEquals(settings.getFontFamily(), family.getValue().getValue());
        	assertEquals(settings.getFontStyle(), style.getValue().getValue());
        	assertEquals(settings.getLineCapAsString(), lineCap.getValue().getValue());
        	color = settings.getFill();
        	assertTrue(color.getRed() == fillColor.getRed());
        	assertTrue(color.getGreen() == fillColor.getBlue());
        	assertTrue(color.getBlue() == fillColor.getGreen());
        	assertTrue(color.getAlpha() == fillColor.getAlpha());
        	color = settings.getStroke();
        	assertTrue(color.getRed() == fillColor.getRed());
        	assertTrue(color.getGreen() == fillColor.getBlue());
        	assertTrue(color.getBlue() == fillColor.getGreen());
        	assertTrue(color.getAlpha() == fillColor.getAlpha());
        	assertTrue(settings.getStrokeWidth() == strokeWidth);
		}
    }
    /**
     * Tests the creation of an ROI not linked to an image.
     * @throws Exception  Thrown if an error occurred.
     */
    @Test(enabled = false)
    public void testCreateROIWithoutImage()
    	throws Exception
    {
    	ROI roi = new ROII();
    	roi.setDescription(omero.rtypes.rstring("roi w/o image"));
    	ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
    	assertNotNull(serverROI);
    }
    
    /**
     * Tests the creation of a ROI.
     * @throws Exception  Thrown if an error occurred.
     */
    @Test
    public void testCreateROI()
    	throws Exception
    {
    	Image image = createBasicImage();
    	ROI roi = new ROII();
    	roi.setImage(image);
    	String name = "roi name";
    	String description = "roi description";
    	String ns = "roi ns";
    	roi.setName(rstring(name));
    	roi.setDescription(rstring(description));
    	roi.setNamespace(rstring(ns));
    	ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
    	assertNotNull(serverROI);
    	ROIData data = new ROIData(serverROI);
    	assertEquals(data.getName(), name);
    	assertEquals(data.getDescription(), description);
    	assertEquals(data.getNamespace(), ns);
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Polygons and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithPolygon() 
    	throws Exception
    {
       	Image image = createBasicImage();
       	ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        double w = 11;
        int z = 0;
        int t = 0;
        int c = 0;
        String points = "10,10 11,11";
        Polygon rect = new PolygonI();
        rect.setPoints(omero.rtypes.rstring(points));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        PolygonData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (PolygonData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getPoints().size() == 1);
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Polylines and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithPolyline() 
    	throws Exception
    {
    	Image image = createBasicImage();
    	IPixelsPrx svc = factory.getPixelsService();
     	List<IObject> values = svc.getAllEnumerations(Marker.class.getName());
     	Marker marker = (Marker) values.get(0);
    	ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        String points = "10,10 11,11";
        int z = 0;
        int t = 0;
        int c = 0;
        String start = "start";
        String end = "end";
        Polyline rect = new PolylineI();
        rect.setPoints(omero.rtypes.rstring(points));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        rect.setMarkerStart(marker);
        rect.setMarkerEnd(marker);
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        PolylineData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (PolylineData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getPoints().size() == 1);
        	assertEquals(shape.getMarkerStart(), marker.getValue().getValue());
        	assertEquals(shape.getMarkerEnd(), marker.getValue().getValue());
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Lines and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithLine() 
    	throws Exception
    {
        Image image = createBasicImage();
        //Get marker enumeration
        IPixelsPrx svc = factory.getPixelsService();
    	List<IObject> values = svc.getAllEnumerations(Marker.class.getName());
    	Marker marker = (Marker) values.get(0);
    	
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        double w = 11;
        int z = 0;
        int t = 0;
        int c = 0;
        String start = "start";
        String end = "end";
        Line rect = new LineI();
        rect.setX1(omero.rtypes.rdouble(v));
        rect.setY1(omero.rtypes.rdouble(v));
        rect.setX2(omero.rtypes.rdouble(w));
        rect.setY2(omero.rtypes.rdouble(w));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        rect.setMarkerStart(marker);
        rect.setMarkerEnd(marker);
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        LineData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (LineData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getX1() == v);
        	assertTrue(shape.getY1() == v);
        	assertTrue(shape.getX2() == w);
        	assertTrue(shape.getY2() == w);
        	assertEquals(shape.getMarkerStart(), marker.getValue().getValue());
        	assertEquals(shape.getMarkerEnd(), marker.getValue().getValue());
		}
    }
    
    /**
	 * Tests the creation of ROIs whose shapes are Masks and converts them 
	 * into the corresponding <code>POJO</code> objects.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateROIWithMask() 
    	throws Exception
    {
        Image image = createBasicImage();
        ROI roi = new ROII();
        roi.setImage(image);
        ROI serverROI = (ROI) iUpdate.saveAndReturnObject(roi);
        assertNotNull(serverROI);
        double v = 10;
        int z = 0;
        int t = 0;
        int c = 0;
        Mask rect = new MaskI();
        rect.setX(omero.rtypes.rdouble(v));
        rect.setY(omero.rtypes.rdouble(v));
        rect.setWidth(omero.rtypes.rdouble(v));
        rect.setHeight(omero.rtypes.rdouble(v));
        rect.setTheZ(omero.rtypes.rint(z));
        rect.setTheT(omero.rtypes.rint(t));
        rect.setTheC(omero.rtypes.rint(c));
        serverROI.addShape(rect);
        
        serverROI = (ROI) iUpdate.saveAndReturnObject(serverROI);
        
        ROIData data = new ROIData(serverROI);
        assertTrue(data.getId() == serverROI.getId().getValue());
        assertTrue(data.getShapeCount() == 1);
        
        List<ShapeData> shapes = data.getShapes(z, t);
        assertNotNull(shapes);
        assertTrue(shapes.size() == 1);
        MaskData shape;
        Iterator<ShapeData> i = shapes.iterator();
        while (i.hasNext()) {
        	shape = (MaskData) i.next();
        	assertTrue(shape.getT() == t);
        	assertTrue(shape.getZ() == z);
        	assertTrue(shape.getC() == c);
        	assertTrue(shape.getX() == v);
        	assertTrue(shape.getY() == v);
        	assertTrue(shape.getWidth() == v);
        	assertTrue(shape.getHeight() == v);
		}
    }
    
    /**
	 * Tests the creation of an instrument using the <code>Add</code> methods
	 * associated to an instrument.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateInstrumentUsingAdd() 
    	throws Exception
    {
    	Instrument instrument;
    	ParametersI param;
    	String sql;
    	IObject test;
    	String value;
    	for (int i = 0; i < ModelMockFactory.LIGHT_SOURCES.length; i++) {
    		value = ModelMockFactory.LIGHT_SOURCES[i];
    		instrument = mmFactory.createInstrument(value);
        	instrument = (Instrument) iUpdate.saveAndReturnObject(instrument);
        	assertNotNull(instrument);
    		param = new ParametersI();
        	param.addLong("iid", instrument.getId().getValue());
        	sql = "select d from Detector as d where d.instrument.id = :iid";
            test = iQuery.findByQuery(sql, param);
            assertNotNull(test);
            sql = "select d from Dichroic as d where d.instrument.id = :iid";
            test = iQuery.findByQuery(sql, param);
            assertNotNull(test);
            sql = "select d from Filter as d where d.instrument.id = :iid";
            test = iQuery.findByQuery(sql, param);
            assertNotNull(test);
            sql = "select d from Objective as d where d.instrument.id = :iid";
            test = iQuery.findByQuery(sql, param);
            assertNotNull(test);
            sql = "select d from LightSource as d where d.instrument.id = :iid";
            test = iQuery.findByQuery(sql, param);
            assertNotNull(test);
            param = new ParametersI();
        	param.addLong("iid", test.getId().getValue());
            if (ModelMockFactory.LASER.equals(value)) {
            	sql = "select d from Laser as d where d.id = :iid";
            	test = iQuery.findByQuery(sql, param);
            	assertNotNull(test);
            } else if (ModelMockFactory.FILAMENT.equals(value)) {
            	sql = "select d from Filament as d where d.id = :iid";
            	test = iQuery.findByQuery(sql, param);
            	assertNotNull(test);
            } else if (ModelMockFactory.ARC.equals(value)) {
            	sql = "select d from Arc as d where d.id = :iid";
            	test = iQuery.findByQuery(sql, param);
            	assertNotNull(test);
            } else if (ModelMockFactory.LIGHT_EMITTING_DIODE.equals(value)) {
            	sql = "select d from LightEmittingDiode as d where d.id = :iid";
            	test = iQuery.findByQuery(sql, param);
            	assertNotNull(test);
            }
		}
    }
    
    /**
	 * Tests the creation of an instrument using the <code>setInstrument</code> 
	 * method on the entities composing the instrument.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test
    public void testCreateInstrumentUsingSet() 
    	throws Exception
    {
    	Instrument instrument = (Instrument) iUpdate.saveAndReturnObject(
    			mmFactory.createInstrument());
    	assertNotNull(instrument);
    	
    	Detector d = mmFactory.createDetector();
    	d.setInstrument((Instrument) instrument.proxy());
    	d = (Detector) iUpdate.saveAndReturnObject(d);
    	assertNotNull(d);
    	
    	Filter f = mmFactory.createFilter(500, 560);
    	f.setInstrument((Instrument) instrument.proxy());
    	f = (Filter) iUpdate.saveAndReturnObject(f);
    	assertNotNull(f);
    	
    	Dichroic di = mmFactory.createDichroic();
    	di.setInstrument((Instrument) instrument.proxy());
    	di = (Dichroic) iUpdate.saveAndReturnObject(di);
    	assertNotNull(di);
    	
    	Objective o = mmFactory.createObjective();
    	o.setInstrument((Instrument) instrument.proxy());
    	o = (Objective) iUpdate.saveAndReturnObject(o);
    	assertNotNull(o);
    	
    	Laser l = mmFactory.createLaser();
    	l.setInstrument((Instrument) instrument.proxy());
    	l = (Laser) iUpdate.saveAndReturnObject(l);
    	assertNotNull(l);
    	
    	ParametersI param = new ParametersI();
    	param.addLong("iid", instrument.getId().getValue());
    	//Now check that we have a detector.
    	String sql = "select d from Detector as d where d.instrument.id = :iid";
        IObject test = iQuery.findByQuery(sql, param);
        assertNotNull(test);
        assertNotNull(test.getId().getValue() == d.getId().getValue());
        sql = "select d from Dichroic as d where d.instrument.id = :iid";
        test = iQuery.findByQuery(sql, param);
        assertNotNull(test);
        assertNotNull(test.getId().getValue() == di.getId().getValue());
        sql = "select d from Filter as d where d.instrument.id = :iid";
        test = iQuery.findByQuery(sql, param);
        assertNotNull(test);
        assertNotNull(test.getId().getValue() == f.getId().getValue());
        sql = "select d from Objective as d where d.instrument.id = :iid";
        test = iQuery.findByQuery(sql, param);
        assertNotNull(test);
        assertNotNull(test.getId().getValue() == o.getId().getValue());
        sql = "select d from LightSource as d where d.instrument.id = :iid";
        test = iQuery.findByQuery(sql, param);
        assertNotNull(test);
    }
    
    /**
	 * Tests to delete various types of annotations i.e. 
	 * Boolean, comment, long, tag, file annotation. 
	 * This method the <code>deleteObject</code> method.
	 * Annotation should be deleted using the deleteQueue method cf. delete
	 * tests.
	 * 
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test(enabled = false, groups = {"ticket:2705"})
    public void testDeleteAnnotation() 
    	throws Exception
    {
    	//creation and linkage have already been tested
    	//boolean
    	BooleanAnnotation b = new BooleanAnnotationI();
    	b.setBoolValue(omero.rtypes.rbool(true));
    	Annotation data = (Annotation) iUpdate.saveAndReturnObject(b);
    	//delete and check
    	long id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	ParametersI param = new ParametersI();
    	param.addId(id);
    	String sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	//long
    	LongAnnotation l = new LongAnnotationI();
    	l.setLongValue(omero.rtypes.rlong(1L));
    	data = (Annotation) iUpdate.saveAndReturnObject(l);
    	id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	param = new ParametersI();
    	param.addId(id);
    	sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	//comment
    	CommentAnnotation c = new CommentAnnotationI();
    	c.setTextValue(omero.rtypes.rstring("comment"));
    	data = (Annotation) iUpdate.saveAndReturnObject(c);
    	id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	param = new ParametersI();
    	param.addId(id);
    	sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	//tag
    	TagAnnotation t = new TagAnnotationI();
    	t.setTextValue(omero.rtypes.rstring("tag"));
    	data = (Annotation) iUpdate.saveAndReturnObject(t);
    	id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	param = new ParametersI();
    	param.addId(id);
    	sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	//File 
    	OriginalFile of = (OriginalFile) iUpdate.saveAndReturnObject(
    			mmFactory.createOriginalFile());
		FileAnnotation fa = new FileAnnotationI();
		fa.setFile(of);
		long ofId = of.getId().getValue();
		data = (Annotation) iUpdate.saveAndReturnObject(fa);
		id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	param = new ParametersI();
    	param.addId(id);
    	sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	param = new ParametersI();
    	param.addId(ofId);
    	//See ticket #2705
    	sql = "select a from OriginalFile as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    	
    	//Term
    	TermAnnotation term = new TermAnnotationI();
    	term.setTermValue(omero.rtypes.rstring("term"));
    	data = (Annotation) iUpdate.saveAndReturnObject(term);
    	id = data.getId().getValue();
    	iUpdate.deleteObject(data);
    	param = new ParametersI();
    	param.addId(id);
    	sql = "select a from Annotation as a where a.id = :id";
    	assertNull(iQuery.findByQuery(sql, param));
    }

    /**
     * Tests the creation of a plate and reagent
     * 
     * @throws Exception Thrown if an error occurred.
     */
    @Test
    public void testPlateAndReagent()
		throws Exception
    {
    	Screen s = mmFactory.simpleScreenData().asScreen();
    	Reagent r = mmFactory.createReagent();
    	s.addReagent(r);
    	Plate p = mmFactory.createPlateWithReagent(1, 1, 1, r);
    	p = savePlate(iUpdate, p, false);
    	s.linkPlate(p);
    	s = (Screen) iUpdate.saveAndReturnObject(s);
    	
    	assertNotNull(s);
    	assertNotNull(s.getName().getValue());
    	assertNotNull(s.getDescription().getValue());
    	assertNotNull(s.getProtocolDescription().getValue());
    	assertNotNull(s.getProtocolIdentifier().getValue());
    	assertNotNull(s.getReagentSetDescription().getValue());
    	assertNotNull(s.getReagentSetIdentifier().getValue());
    	
    	//reagent first
    	String sql = "select r from Reagent as r ";
    	sql += "join fetch r.screens as s ";
    	sql += "where s.id = :id";
    	ParametersI param = new ParametersI();
    	param.addId(s.getId().getValue());
    	r = (Reagent) iQuery.findByQuery(sql, param);
    	assertNotNull(r);
    	assertNotNull(r.getName().getValue());
    	assertNotNull(r.getDescription().getValue());
    	assertNotNull(r.getReagentIdentifier().getValue());
    	
    	//
    	sql = "select s from ScreenPlateLink as s ";
    	sql += "join fetch s.child as c ";
    	sql += "join fetch s.parent as p ";
    	sql += "where p.id = :id";
    	param = new ParametersI();
    	param.addId(s.getId().getValue());
    	ScreenPlateLink link = (ScreenPlateLink) iQuery.findByQuery(sql, param);
    	assertNotNull(link);
    }
    
    /**
     * Tests to create a file annotation and link it to several images using
     * the saveAndReturnArray.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "ticket:5370" })
    public void testAttachFileAnnotationToSeveralImages()
    	throws Exception 
    {
    	OriginalFile of = (OriginalFile) iUpdate.saveAndReturnObject(
    			mmFactory.createOriginalFile());
		assertNotNull(of);
		FileAnnotation fa = new FileAnnotationI();
		fa.setFile(of);
		FileAnnotation data = (FileAnnotation) iUpdate.saveAndReturnObject(fa);
		assertNotNull(data);
		//Image
        Image i1 = createBasicImage();
        Image i2 = createBasicImage();
        
        List<IObject> links = new ArrayList<IObject>();
        ImageAnnotationLink l = new ImageAnnotationLinkI();
        l.setParent((Image) i1.proxy());
        l.setChild((Annotation) data.proxy());
        links.add(l);
        l = new ImageAnnotationLinkI();
        l.setParent((Image) i2.proxy());
        l.setChild((Annotation) data.proxy());
        links.add(l);
        links = iUpdate.saveAndReturnArray(links);
        assertNotNull(links);
        assertEquals(links.size(), 2);
        Iterator<IObject> i = links.iterator();
        long id;
        List<Long> ids = new ArrayList<Long>();
        ids.add(i1.getId().getValue());
        ids.add(i2.getId().getValue());
        int n = 0;
        while (i.hasNext()) {
			l = (ImageAnnotationLink) i.next();
			assertEquals(l.getChild().getId().getValue(),
					data.getId().getValue());
			id = l.getParent().getId().getValue();
			if (ids.contains(id)) n++;
		}
        assertEquals(ids.size(), n);
    }

    /**
     * Tests to create a file annotation and link it to several images using
     * the saveAndReturnArray.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(groups = { "ticket:5370" })
    public void testAttachFileAnnotationToSeveralImagesII()
    	throws Exception 
    {
    	OriginalFile of = (OriginalFile) iUpdate.saveAndReturnObject(
    			mmFactory.createOriginalFile());
		assertNotNull(of);
		FileAnnotation fa = new FileAnnotationI();
		fa.setFile(of);
		FileAnnotation data = (FileAnnotation) iUpdate.saveAndReturnObject(fa);
		assertNotNull(data);
		
        Image i1 = createBasicImage();
        Image i2 = createBasicImage();
        
        ImageAnnotationLink l = new ImageAnnotationLinkI();
        l.setParent((Image) i1.proxy());
        l.setChild((Annotation) data.proxy());
        
        l =  (ImageAnnotationLink) iUpdate.saveAndReturnObject(l);
        assertEquals(l.getChild().getId().getValue(),
				data.getId().getValue());
        assertEquals(l.getParent().getId().getValue(),
				i1.getId().getValue());
        l = new ImageAnnotationLinkI();
        l.setParent((Image) i2.proxy());
        l.setChild((Annotation) data.proxy());
        l =  (ImageAnnotationLink) iUpdate.saveAndReturnObject(l);
        
        assertEquals(l.getChild().getId().getValue(),
				data.getId().getValue());
        assertEquals(l.getParent().getId().getValue(),
				i2.getId().getValue());
    }
    
    /**
	 * Tests to delete comment annotation linked to a plate acquisition.
	 * 
	 * @throws Exception  Thrown if an error occurred.
	 */
    @Test(enabled = true)
    public void testDeleteCommentAnnotationLinkedToObject()
    	throws Exception
    {
    	//comment linked to project
    	Project project = new ProjectI();
    	project.setName(omero.rtypes.rstring("p"));
    	project = (Project) iUpdate.saveAndReturnObject(project);
    	CommentAnnotation cp = new CommentAnnotationI();
    	cp.setTextValue(omero.rtypes.rstring("comment"));
    	cp = (CommentAnnotation) iUpdate.saveAndReturnObject(cp);
    	
    	ProjectAnnotationLink lpc = new ProjectAnnotationLinkI();
    	lpc.setChild((Annotation) cp.proxy());
    	lpc.setParent((Project) project.proxy());
    	IObject o = iUpdate.saveAndReturnObject(lpc);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(cp);
    	String sql = "select a from Annotation as a ";
		sql += "where a.id = :id";
		ParametersI param = new ParametersI();
    	param.addId(cp.getId().getValue());
    	List<IObject> results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    	

    	//comment linked to dataset
    	Dataset dataset = new DatasetI();
    	dataset.setName(omero.rtypes.rstring("p"));
    	dataset = (Dataset) iUpdate.saveAndReturnObject(dataset);
    	cp = new CommentAnnotationI();
    	cp.setTextValue(omero.rtypes.rstring("comment"));
    	cp = (CommentAnnotation) iUpdate.saveAndReturnObject(cp);
    	
    	DatasetAnnotationLink ldc = new DatasetAnnotationLinkI();
    	ldc.setChild((Annotation) cp.proxy());
    	ldc.setParent(dataset);
    	o = iUpdate.saveAndReturnObject(ldc);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(cp);
		param = new ParametersI();
    	param.addId(cp.getId().getValue());
    	results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    	
    	//comment linked to screen
    	Screen screen = new ScreenI();
    	screen.setName(omero.rtypes.rstring("p"));
    	
    	screen = (Screen) iUpdate.saveAndReturnObject(screen);
    	cp = new CommentAnnotationI();
    	cp.setTextValue(omero.rtypes.rstring("comment"));
    	cp = (CommentAnnotation) iUpdate.saveAndReturnObject(cp);
    	
    	//Comment linked to a screen
    	ScreenAnnotationLink lsc = new ScreenAnnotationLinkI();
    	lsc.setChild((Annotation) cp.proxy());
    	lsc.setParent(screen);
    	o = iUpdate.saveAndReturnObject(lsc);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(cp);
		param = new ParametersI();
    	param.addId(cp.getId().getValue());
    	results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    	
    	//comment linked to plate
    	Plate plate = new PlateI();
    	plate.setName(omero.rtypes.rstring("p"));
    	plate = savePlate(iUpdate, plate, false);
    	cp = new CommentAnnotationI();
    	cp.setTextValue(omero.rtypes.rstring("comment"));
    	cp = (CommentAnnotation) iUpdate.saveAndReturnObject(cp);
    	
    	PlateAnnotationLink lppc = new PlateAnnotationLinkI();
    	lppc.setChild((Annotation) cp.proxy());
    	lppc.setParent(plate);
    	o = iUpdate.saveAndReturnObject(o);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(cp);
		param = new ParametersI();
    	param.addId(cp.getId().getValue());
    	results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    	
    	//comment linked to image
    	Image image = createImage(1,1,1,1,1,"p");
    	cp = new CommentAnnotationI();
    	cp.setTextValue(omero.rtypes.rstring("comment"));
    	cp = (CommentAnnotation) iUpdate.saveAndReturnObject(cp);
    	
    	ImageAnnotationLink lic = new ImageAnnotationLinkI();
    	lic.setChild((Annotation) cp.proxy());
    	lic.setParent(image);
    	o = iUpdate.saveAndReturnObject(lic);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(cp);
		param = new ParametersI();
    	param.addId(cp.getId().getValue());
    	results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    	
    	
    	
    	Plate newPlate = mmFactory.createPlate(1, 1, 1, 1, false);
    	newPlate = savePlate(iUpdate, newPlate, false);
		sql = "select pa from PlateAcquisition as pa ";
		sql += "where pa.plate.id = :id";
		ParametersI plateSQLParams = new ParametersI();
		plateSQLParams.addId(newPlate.getId().getValue());
    	List<IObject> plateAquisitions = iQuery.findAllByQuery(sql, plateSQLParams);
    	//Delete the first one.
    	PlateAcquisition pa = (PlateAcquisition) plateAquisitions.get(0);
    	
    	CommentAnnotation c = new CommentAnnotationI();
    	c.setTextValue(omero.rtypes.rstring("comment"));
    	c = (CommentAnnotation) iUpdate.saveAndReturnObject(c);
    	long id = c.getId().getValue();
    	
    	PlateAcquisitionAnnotationLink link = new PlateAcquisitionAnnotationLinkI();
    	link.setChild(c);
    	link.setParent(pa);
    	
    	o = iUpdate.saveAndReturnObject(link);
    	iUpdate.deleteObject(o);
    	iUpdate.deleteObject(c);
		//delete(iDelete, client, new DeleteCommand("/Annotation", id, null));
		sql = "select a from Annotation as a ";
		sql += "where a.id = :id";
		param = new ParametersI();
    	param.addId(id);
    	results = iQuery.findAllByQuery(sql, param);
    	assertEquals(results.size(), 0);
    }
}
