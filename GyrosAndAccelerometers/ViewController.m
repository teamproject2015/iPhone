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
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
