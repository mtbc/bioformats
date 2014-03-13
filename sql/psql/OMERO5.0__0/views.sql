--
-- Copyright 2008 Glencoe Software, Inc. All rights reserved.
-- Use is subject to license terms supplied in LICENSE.txt
--

-- This file was generated by dsl/resources/ome/dsl/views.vm
-- and can be used to overwrite the generated Map<Long, Long> tables
-- with functional views.

  DROP TABLE count_Annotation_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Annotation_annotationLinks_by_owner (Annotation_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM annotationannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Channel_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Channel_annotationLinks_by_owner (Channel_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM channelannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_projectLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_projectLinks_by_owner (Dataset_id, owner_id, count) AS select child, owner_id, count(*)
    FROM projectdatasetlink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Dataset_imageLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_imageLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM datasetimagelink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_annotationLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM datasetannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Fileset_jobLinks_by_owner;

  CREATE OR REPLACE VIEW count_Fileset_jobLinks_by_owner (Fileset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM filesetjoblink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Fileset_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Fileset_annotationLinks_by_owner (Fileset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM filesetannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Filter_excitationFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_Filter_excitationFilterLink_by_owner (Filter_id, owner_id, count) AS select child, owner_id, count(*)
    FROM filtersetexcitationfilterlink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Filter_emissionFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_Filter_emissionFilterLink_by_owner (Filter_id, owner_id, count) AS select child, owner_id, count(*)
    FROM filtersetemissionfilterlink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_FilterSet_excitationFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_FilterSet_excitationFilterLink_by_owner (FilterSet_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM filtersetexcitationfilterlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_FilterSet_emissionFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_FilterSet_emissionFilterLink_by_owner (FilterSet_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM filtersetemissionfilterlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Image_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_datasetLinks_by_owner (Image_id, owner_id, count) AS select child, owner_id, count(*)
    FROM datasetimagelink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Image_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_annotationLinks_by_owner (Image_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM imageannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Job_originalFileLinks_by_owner;

  CREATE OR REPLACE VIEW count_Job_originalFileLinks_by_owner (Job_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM joboriginalfilelink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_LightPath_excitationFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_LightPath_excitationFilterLink_by_owner (LightPath_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM lightpathexcitationfilterlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_LightPath_emissionFilterLink_by_owner;

  CREATE OR REPLACE VIEW count_LightPath_emissionFilterLink_by_owner (LightPath_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM lightpathemissionfilterlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Namespace_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Namespace_annotationLinks_by_owner (Namespace_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM namespaceannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_pixelsFileMaps_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM pixelsoriginalfilemap GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_annotationLinks_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM originalfileannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Pixels_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_pixelsFileMaps_by_owner (Pixels_id, owner_id, count) AS select child, owner_id, count(*)
    FROM pixelsoriginalfilemap GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Pixels_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_annotationLinks_by_owner (Pixels_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM pixelsannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_PlaneInfo_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_PlaneInfo_annotationLinks_by_owner (PlaneInfo_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM planeinfoannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Plate_screenLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_screenLinks_by_owner (Plate_id, owner_id, count) AS select child, owner_id, count(*)
    FROM screenplatelink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Plate_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_annotationLinks_by_owner (Plate_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM plateannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_PlateAcquisition_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_PlateAcquisition_annotationLinks_by_owner (PlateAcquisition_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM plateacquisitionannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_datasetLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM projectdatasetlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_annotationLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM projectannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Reagent_wellLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_wellLinks_by_owner (Reagent_id, owner_id, count) AS select child, owner_id, count(*)
    FROM wellreagentlink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Reagent_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_annotationLinks_by_owner (Reagent_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM reagentannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Roi_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Roi_annotationLinks_by_owner (Roi_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM roiannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_plateLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_plateLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM screenplatelink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_annotationLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM screenannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_reagentLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_reagentLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM wellreagentlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_annotationLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM wellannotationlink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_WellSample_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_annotationLinks_by_owner (WellSample_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM wellsampleannotationlink GROUP BY parent, owner_id ORDER BY parent;

