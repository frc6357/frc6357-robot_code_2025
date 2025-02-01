// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Konstants.ElevatorConstants.ElevatorPosition;
import frc.robot.subsystems.SK25Elevator;

public class ElevatorButtonCommand extends Command
{
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})

    private final SK25Elevator      elevator;
    private final ElevatorPosition  position;

    /**
     * This command allows the operator to set the angle of the arm to a specified
     * position.
     * 
     * @param position
     *            The position to set the arm to
     * @param Elevator
     *            The Elevator subsystem the command operates on.
     */
    public ElevatorButtonCommand(ElevatorPosition position, SK25Elevator elevator)
    {
        this.position = position;
        this.elevator = elevator;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(elevator);
    }

    @Override
    public void initialize()
    {
        elevator.setTargetHeight(position);
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished()
    {
        if(DriverStation.isAutonomousEnabled())
        {
            if (elevator.isRightAtTargetPosition() && elevator.isRightAtTargetPosition())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    
        
    }
}