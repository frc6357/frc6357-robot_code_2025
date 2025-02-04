package frc.robot.bindings;

import static frc.robot.Ports.OperatorPorts.climbLowerButton;
import static frc.robot.Ports.OperatorPorts.climbRaiseButton;

import java.util.Optional;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.ClimbCommand1;
import frc.robot.commands.ClimbCommandReturn;
import frc.robot.subsystems.SK25Climb;



public class ClimbBinder implements CommandBinder {
    Optional<SK25Climb> subsystem;
    Trigger raise;
    Trigger lower;

    public ClimbBinder(Optional<SK25Climb> climbSys) {
        subsystem = climbSys;
        raise = climbRaiseButton.button;
        lower = climbLowerButton.button;
    }

    public void bindButtons() 
    {
        if (subsystem.isPresent()) 
        {
            SK25Climb subsys = subsystem.get();
           raise.onTrue(new ClimbCommand1(subsys));
           lower.onTrue(new ClimbCommandReturn(subsys));
           //raise.onTrue(new InstantCommand(() -> subsys.runMotor(kSpeed)));
           //lower.onTrue(new InstantCommand(() -> subsys.runMotor(-kSpeed)));
        }
    }
}
