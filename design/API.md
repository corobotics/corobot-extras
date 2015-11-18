# Corobot Client-Server API Communication

Server (Robot) and Client sides of the API communicate over TCP messages 
in the following format:

    <Message_ID> <Message_Type> <Message_Data>\n

The client must generate a unique message ID for each command sent to the robot.
Reponses to that command will have the same ID.
Commands must end with a '\n' newline.

## Messages

### Position Data

Get the robot's current position (as best known by itself).

**Request:**

    GETPOS

**Response:**

    POS <X> <Y> <Theta>

### Navigation Directives

Move the robot to either a named location on the map or a specific (x, y).
`GOTO` commands will attempt to drive in a straight line to the destination.
`NAV` commands will perform an A* search through the waypoint graph and
follow the best route to the destination.

**Requests:**

    GOTOXY <Map_X> <Map_Y>
    GOTOLOC <Landmark_Name>
    NAVTOXY <Map_X> <Map_Y>
    NAVTOLOC <Landmark_Name>

**Response:**

    ARRIVED

### Robot UI Messages

Post a message to the laptop's screen, with the option of requesting 
confirmation of the message.

**Requests:**

    SHOW_MSG <timeout> <text>
    SHOW_MSG_CONFIRM <timeout> <text>

**Response:**

    CONFIRM <bool>
    
### Face Recognition

Attempts to recognize faces using the head camera of the Corobot laptop.
'FACEREC' command will wait for a recognition and return a single result, including the location.
'LASTSEEN_n' will return the last n results. 

**Requests:**
    
    FACEREC
    LASTSEEN_n

**Response:**
    
    FACEREC <subject> <confidence> <pos_x> <pos_y> 
    LASTSEEN_n <subject> <confidence> <pos_x> <pos_y>

### Errors

Any request can also get an error response if something goes wrong:

    ERROR <Error_Message>
