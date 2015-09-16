//
//  ViewController.m
//  GyrosAndAccelerometers
//


#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

    
    self.motionManager = [[CMMotionManager alloc] init];
    self.motionManager.accelerometerUpdateInterval = .2;
    self.motionManager.gyroUpdateInterval = .2;
    self.motionManager.magnetometerUpdateInterval=.2;
    // 20ms
    
    [self.motionManager startAccelerometerUpdatesToQueue:[NSOperationQueue currentQueue]
                                             withHandler:^(CMAccelerometerData *accelerometerData, NSError *error) {
                                                 [self outputAccelertionData:accelerometerData.acceleration];
                                                 if(error){
                                                     
                                                     NSLog(@"%@", error);
                                                 }
    }];
    
    [self.motionManager startGyroUpdatesToQueue:[NSOperationQueue currentQueue]
                                    withHandler:^(CMGyroData *gyroData, NSError *error) {
                                        [self outputRotationData:gyroData.rotationRate];
                                        if(error){
                                            
                                            NSLog(@"%@", error);
                                        }
                                    }];

[self.motionManager startMagnetometerUpdatesToQueue:[NSOperationQueue currentQueue]
                                        withHandler:^(CMMagnetometerData *magnetometerData, NSError *error){
                                            [self outputMagnetometerData:magnetometerData.magneticField];
                                            if(error){
                                                
                                                NSLog(@"%@", error);
                                            }
                                        }];
 }

-(void)outputAccelertionData:(CMAcceleration)acceleration
{
    self.accX.text = [NSString stringWithFormat:@" %.2fg",acceleration.x];
    self.accY.text = [NSString stringWithFormat:@" %.2fg",acceleration.y];
    self.accZ.text = [NSString stringWithFormat:@" %.2fg",acceleration.z];

}
-(void)outputRotationData:(CMRotationRate)rotation
{
    
    self.rotX.text = [NSString stringWithFormat:@" %.2fr/s",rotation.x];
    self.rotY.text = [NSString stringWithFormat:@" %.2fr/s",rotation.y];
    self.rotZ.text = [NSString stringWithFormat:@" %.2fr/s",rotation.z];

}
-(void)outputMagnetometerData:(CMMagneticField)magnetic
{
        
    self.magX.text = [NSString stringWithFormat:@" %.2fr/s",magnetic.x];
    self.magY.text = [NSString stringWithFormat:@" %.2fr/s",magnetic.y];
    self.magZ.text = [NSString stringWithFormat:@" %.2fr/s",magnetic.z];
        
}

//save data into a csv file
-(IBAction)saveInfo:(id)sender{
    NSString *resultLine=[NSString stringWithFormat:@"%@,%@,%@,%@,%@,%@,%@,%@,%@\n",
                          self.accX.text,
                          self.accY.text,
                          self.accZ.text,
                          self.rotX.text,
                          self.rotY.text,
                          self.rotZ.text,
                          self.magX.text,
                          self.magY.text,
                          self.magZ.text];
    NSString *docPath =[NSSearchPathForDirectoriesInDomains(NSDocumentationDirectory,NSUserDomainMask, YES)objectAtIndex:0];
    //resultView.text=docPath;
    NSString *sensorsdata = [docPath stringByAppendingPathComponent:@"results.csv"];
    if (![[NSFileManager defaultManager] fileExistsAtPath:docPath]){
        [[NSFileManager defaultManager]
    createFileAtPath:sensorsdata contents:nil attributes:nil];
    }
    NSFileHandle *fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:sensorsdata];
    [fileHandle seekToEndOfFile];
    [fileHandle writeData:[resultLine dataUsingEncoding:NSUTF8StringEncoding]];
    [fileHandle closeFile];
    self.accX.text=@"";
    self.accY.text=@"";
    self.accZ.text=@"";
    self.rotX.text=@"";
    self.rotY.text=@"";
    self.rotZ.text=@"";
    self.magX.text=@"";
    self.magY.text=@"";
    self.magZ.text=@"";
    NSLog(@"Data saved");
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
