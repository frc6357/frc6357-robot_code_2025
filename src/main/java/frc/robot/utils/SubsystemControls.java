package frc.robot.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class holds the subsystem control values as imported from the subsystem control
 * JSON file. This is made for the 2022 season
 */
public class SubsystemControls
{

    private final boolean swerve;
    private final boolean example;
    private final boolean elevator;
    private final boolean endEffector;

     /**  
     * @param swerve
     *            indicates if the swerve subsystem is present and should be enabled
     * @param example
     *            indicates if the example subsystem is present and should be enabled
     */
    public SubsystemControls(
        @JsonProperty(required = true, value = "swerve")      boolean swerve,
        @JsonProperty(required = true, value = "example")      boolean example,
        @JsonProperty(required = true, value = "elevator")      boolean elevator,
        @JsonProperty(required = true, value = "endeffector") boolean endeffector
    )
    {
        this.swerve = swerve;
        this.example = example;
        this.elevator = elevator;
        this.endEffector = endeffector;
    }


    /**
     * Returns true if the drive subsystem is indicated as present and should be enabled.
     * 
     * @return true if the drive subsystem is indicated as present and should be enabled; false
     *         otherwise
     */
    public boolean isSwervePresent()
    {
        return swerve;
    }
    public boolean isExamplePresent()
    {
        return example;
    }
    public boolean isElevatorPresent()
    {
        return elevator;
    }
    public boolean isEndEffectorPresent()
    {
        return endEffector;
    }
}
