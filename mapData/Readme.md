# Landmark Data

## pointDataMap.xcf/png

Landmark points are overlayed on the map of the 3rd floor.  
This gives us "map" (pixel) x-y coordinates that can then be converted to "real" (meter) x-y coordinates.

## waypoints.ods

LibreOffice spreadsheet of complete landmark graph data.  
Provides landmark name, coordinates (map and real), and neighbor list.

### CSV Export
Changes to this document **MUST** be saved as comma-separated-value format (Text CSV)
to {corobotics ROS nodes repository location}/corobot_map/map/waypoints.csv

Export Params (For LibreOffice Calc's Save As "Text CSV"):
* Character Set: UTF-8
* Field Delimiter: ,
* Text Delimiter: "
* Save cell contents as shown: Checked
* Quote all text cells: Unchecked
* Fixed column width: Unchecked

### Adding landmarks

* Paint the new landmark in pointDataMap.xcf
  * There is a separate layer for landmark points, USE IT!
  * Use the color red.
* Note pixel X/Y values from pointDataMap.xcf.
* Add new row to spreadsheet and enter pixel X/Y values.
* Drag formula for converting from pixel to meter coordinates from previous rows.
* Export changes to the map node's map data!
