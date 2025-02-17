// Essentials
package frc.robot.subsystems;
import edu.wpi.first.math.MathUtil;

// Constants (Muy Importante)
import static frc.robot.Konstants.ElevatorConstants.kElevatorCurrentLimit;
import static frc.robot.Konstants.ElevatorConstants.kElevatorMotorMaxOutput;
import static frc.robot.Konstants.ElevatorConstants.kElevatorMotorMinOutput;
import static frc.robot.Konstants.ElevatorConstants.kMaxInteg;
import static frc.robot.Konstants.ElevatorConstants.kMinInteg;
import static frc.robot.Konstants.ElevatorConstants.kPositionTolerance;
import static frc.robot.Konstants.ElevatorConstants.leftElevator;
import static frc.robot.Konstants.ElevatorConstants.rightElevator;
import static frc.robot.Ports.ElevatorPorts.kLeftElevatorMotor;
import static frc.robot.Ports.ElevatorPorts.kRightElevatorMotor;

// Encoder V3 (Still REV)
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;

// Motors - Sparkflex
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

// Configurations For Motors (REV)
import com.revrobotics.spark.config.SparkFlexConfig;

// PID Controller
import edu.wpi.first.math.controller.PIDController;

// Limit Switches
import edu.wpi.first.wpilibj.DigitalInput;

// SmartDashboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import frc.robot.Konstants.ElevatorConstants.ElevatorPosition;
import frc.robot.subsystems.superclasses.Elevator;

public class SK25Elevator extends Elevator
{
    // Create Memory Motor Objects
    SparkFlex motorR;
    SparkFlex motorL;

    // REV CLOSED LOOP (V3)
    SparkClosedLoopController closedLoopController;
    RelativeEncoder encoder;

    // Creating Config Object
    SparkFlexConfig motorConfigL;
    SparkFlexConfig motorConfigR;

    //Create Memory PID Objects
    PIDController rPID;
    PIDController lPID;

    // Target & Current Position
    double LtargetHeight;
    double LcurrentHeight;

    double RtargetHeight;
    double RcurrentHeight;

    // Touch Sensor Objects
    DigitalInput touchSensorTop;
    DigitalInput touchSensorBottom;

    // Constructor For Public Command Access
    public SK25Elevator()
    {
        // PID Controllers - Setpoints
        /*
        lPID = new PIDController(leftElevator.kP, leftElevator.kI, leftElevator.kD);
        rPID = new PIDController(rightElevator.kP, rightElevator.kI, rightElevator.kD);
        
        lPID.setIntegratorRange(kMinInteg, kMaxInteg);
        rPID.setIntegratorRange(kMinInteg, kMaxInteg);

        lPID.setSetpoint(0.0);
        rPID.setSetpoint(0.0);
        */
        
        // Motor Initialization With REV Sparkflex - Configurations
        motorL = new SparkFlex(kLeftElevatorMotor.ID, MotorType.kBrushless);
        motorR = new SparkFlex(kRightElevatorMotor.ID, MotorType.kBrushless);

        motorConfigL = new SparkFlexConfig();
        motorConfigR = new SparkFlexConfig();

        // Configurations For The Left Motor & Encoder
        motorConfigL
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(kElevatorCurrentLimit);
        motorConfigL.encoder
            .positionConversionFactor(1)
            .velocityConversionFactor(1);
        motorConfigL.closedLoop
            .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
            .velocityFF(1.0 / 565, ClosedLoopSlot.kSlot0)
            //Set PID values for position control. We don't need to pass a closed loop
            //slot, as it will default to slot 0.
            .p(0.1)
            .i(0)
            .d(0)
            .outputRange(-1, 1)
            //Set PID values for velocity control in slot 1
            .p(0.0001, ClosedLoopSlot.kSlot0)
            .i(0, ClosedLoopSlot.kSlot0)
            .d(0, ClosedLoopSlot.kSlot0)
            .outputRange(-1, 1, ClosedLoopSlot.kSlot0);
        motorConfigL.closedLoop.maxMotion
            // Set MAXMotion parameters for position control. We don't need to pass
            // a closed loop slot, as it will default to slot 0.
            .maxVelocity(1000)
            .maxAcceleration(1000)
            .allowedClosedLoopError(1)
            // Set MAXMotion parameters for velocity control in slot 0
            .maxAcceleration(500, ClosedLoopSlot.kSlot0)
            .maxVelocity(6000, ClosedLoopSlot.kSlot0)
            .allowedClosedLoopError(1, ClosedLoopSlot.kSlot0);
        
        // Apply Motor Configurations
        motorL.configure(motorConfigL, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);

        // Configurations For The Right Motor
        motorConfigR
            .follow(motorL, true)
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(kElevatorCurrentLimit);
            //.inverted(true)

        motorR.configure(motorConfigR, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);

        // Encoder V3
        closedLoopController = motorL.getClosedLoopController();
        encoder = motorL.getEncoder();

        // Current, Target, and Reset Positions
        RtargetHeight = 0.0;
        RcurrentHeight = 0.0;
        
        LtargetHeight = 0.0;
        LcurrentHeight = 0.0;

        closedLoopController.setReference(0, ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0);
    }

    /**
     * {@inheritDoc}
     */
    
    public void setTargetHeight(ElevatorPosition height)
    {
        setTheTargetHeight(height.height);
        //setLeftTargetHeight(height.height);
    }
    

    /**
     * {@inheritDoc}
     */
    /*
    public void setRightTargetHeight(double height)
    {
        RtargetHeight = height;
        rPID.setSetpoint(LtargetHeight);
    }
    */
    
    /**
     * {@inheritDoc}
     */
    public void setTheTargetHeight(double height)
    {
        LtargetHeight = height;
        closedLoopController.setReference(LtargetHeight, ControlType.kMAXMotionPositionControl, ClosedLoopSlot.kSlot0);
        //lPID.setSetpoint(LtargetHeight);
    }

    /**
     * {@inheritDoc}
     */
    public double getEncoderPosition()
    {
        double encoderPositionValue = encoder.getPosition();
        return encoderPositionValue;
    }

    /**
     * {@inheritDoc}
     */
    public double getRightTargetPosition()
    {
        return RtargetHeight;
    }

    /**
     * {@inheritDoc}
     */
    public double getLeftTargetPosition()
    {
        return LtargetHeight;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRightAtTargetPosition()
    {
        return Math.abs(getEncoderPosition() - getRightTargetPosition()) < kPositionTolerance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLeftAtTargetPosition()
    {
        return Math.abs(getEncoderPosition() - getLeftTargetPosition()) < kPositionTolerance;
    }

    // Button Sensor Methods
    public Boolean isTopSensorPressed()
    {
        return !touchSensorTop.get();
    }

    public Boolean isBottomSensorPressed()
    {
        return !touchSensorBottom.get();
    }

    // Button Sensor Methods For Use By Commands
    public boolean atTop()
    {
        if(isTopSensorPressed())
            return true;
        else
            return false;
    }

    public boolean atBottom()
    {
        if(isBottomSensorPressed())
            return true;
        else
            return false;
    }

    @Override
    public void periodic()
    {  
        // Display encoder position and velocity
        SmartDashboard.putNumber("Actual Position", encoder.getPosition());
        SmartDashboard.putNumber("Actual Velocity", encoder.getVelocity());

        // Initialize Current & Target Positions
        double currentPosition = getEncoderPosition();
        double lTargetPosition = getLeftTargetPosition();
        double rTargetPosition = getRightTargetPosition();
        
        // Calculates Motor Speed & Puts It Within Operating Range
        /* 
        double lSpeed = MathUtil.clamp(lPID.calculate(currentPosition), kElevatorMotorMinOutput, kElevatorMotorMaxOutput);
        double rSpeed = MathUtil.clamp(rPID.calculate(currentPosition), kElevatorMotorMinOutput, kElevatorMotorMaxOutput);

        motorL.set(lSpeed); 
        motorR.set(rSpeed); 
        */
        
        // SmartDashboard Current & Target Positions
        SmartDashboard.putNumber("Current Estimated Position", currentPosition);
        SmartDashboard.putNumber("Left Target Position", lTargetPosition);
        SmartDashboard.putBoolean("Left Elevator at Setpoint", isLeftAtTargetPosition());
        SmartDashboard.putNumber("Right Target Position", rTargetPosition);
        SmartDashboard.putBoolean("Right Elevator at Setpoint", isRightAtTargetPosition()); 
    }
    public void testPeriodic(){}
    public void testInit(){}
}