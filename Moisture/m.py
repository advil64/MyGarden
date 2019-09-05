import serial
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db



arduinoData = serial.Serial('/dev/tty96B0',9600);
write_to_file_path = "output.txt";
output_file = open(write_to_file_path, "w+");
cred = credentials.Certificate('/home/linaro/Desktop/Moisture/credentials.json');
firebase_admin.initialize_app(cred, {
    'databaseURL':'https://ratemytreater.firebaseio.com/Sensors/Backyard'
});

root = db.reference('/Sensors/Backyard');

while True:
    line = arduinoData.readline();
    line = line.decode("utf-8");
    root.child('Moisture').set(int(line.rstrip()));
    print(line);
    output_file.write(line);
