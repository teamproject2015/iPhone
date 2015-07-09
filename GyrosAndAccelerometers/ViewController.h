//
//  ViewController.h
//  GyrosAndAccelerometers
//
//  Inspired by codes from NSCookbook

#import <UIKit/UIKit.h>
#import <CoreMotion/CoreMotion.h>



@interface ViewController : UIViewController

@property (strong, nonatomic) IBOutlet UILabel *accX;
@property (strong, nonatomic) IBOutlet UILabel *accY;
@property (strong, nonatomic) IBOutlet UILabel *accZ;

@property (strong, nonatomic) IBOutlet UILabel *rotX;
@property (strong, nonatomic) IBOutlet UILabel *rotY;
@property (strong, nonatomic) IBOutlet UILabel *rotZ;

@property (strong, nonatomic) IBOutlet UILabel *magX;
@property (strong, nonatomic) IBOutlet UILabel *magY;
@property (strong, nonatomic) IBOutlet UILabel *magZ;

@property (strong, nonatomic) CMMotionManager *motionManager;

@end
