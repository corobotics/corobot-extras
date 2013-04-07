Corobot Client-Server API Communication

Server (Robot) and Client sides of the API communicate over TCP messages 
in the following format.

Note: All commands must end with a '\n' newline.

Client Messages (to the server):
GETPOS - Get the robot's current position (as best known by itself).  
            Robot will reply with a POS message (see Robot Messages).

GOTOXY <Map_X> <Map_Y> - Adds a destination (Map_X, Map_Y) to the robot's 
            queue of locations to travel to.

GOTOLOC <Landmark_Name> - Adds a destination (x,y) to the robot's queue of
            locations to travel on after looking up Landmark_Name.

NAVTOLOC <Landmark_Name> - Performs A* search through the graph of landmarks
            and adds the chain of landmarks from the robot's current position
            to the goal given by Landmark_Name to the robot's queue of
            locations to travel to.

Server/Robot Messages (in response to client commands):
POS <X> <Y> <Theta> - The robot's current pose.

ARRIVED - The robot has arrived at a location.

OKAY - Previous message received and processed with no immediate error.

ERROR <Error_Message> - Previous message cased error condition given.