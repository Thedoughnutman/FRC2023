// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.autocommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.PositionConstants;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClawGripSubsystem;

public final class Autos {
    private static boolean blueTeam = true; // Whether we are on the blue team or not
    private static final Timer timer = new Timer();

    /**
     * Example static factory for an autonomous command.
     */
    public static CommandBase exampleAuto(/* if you need a subsystem, pass it in as a parameter here, and make sure to pass them in when the method is called inside RobotContainer */) {
        // You can also pass in commands as parameters if you want to use existing commands as part of your autonomouse command (such as BalanceChargeStation.java *wink wink hint hint*)
        // Example command case
        return Commands.runOnce(() -> {
            // Autonomous scenario code
            
        }) /* susbsystems that are used inside the curly braces above must be passed in here */;

        // To implement a sequence of actions/commands use .andThen(), can be used to implement PathWeaver trajectories
        // To implement simultaneous actions/commands use .alongWith(), can also be used to implement PathWeaver trajectories
        // For example: return Commands.runOnce(() -> {}).andThen(trajectory1Command);
        // Example of simultaneous implementation: return Commands.runOnce(() -> {}).alongWith(trajectory1Command);
    }

    public static CommandBase defaultAuto() {
        // Example command case
        return Commands.runOnce(() -> {
            // Autonomous scenario code
        });

    }

    public static CommandBase balanceAutoFirstHalf(Command driveForwardOverChargeStationBlueCommand, 
            Command driveForwardOverChargeStationRedCommand, ArmSubsystem arm) {

        if (blueTeam) {
            return Commands.runOnce(() -> {
                    // Any neccessary calibration code
                }).alongWith(arm.calibrateArm())
                .alongWith(driveForwardOverChargeStationBlueCommand);
        } else {
            return Commands.runOnce(() -> {
                // Any neccessary calibration code
            }).alongWith(arm.calibrateArm())
            .alongWith(driveForwardOverChargeStationRedCommand);
        }
    }

    public static CommandBase balanceAutoSecondHalf(Command driveBackwardsOntoChargeStationBlueCommand, 
            Command driveBackwardsOntoChargeStationRedCommand, Command balanceChargeStation) {

        if (blueTeam) {    
            return driveBackwardsOntoChargeStationBlueCommand
                .andThen(balanceChargeStation);
        } else {
            return driveBackwardsOntoChargeStationRedCommand
                .andThen(balanceChargeStation);
        }
    }

    // Autonomous mode for balancing charge station
    public static CommandBase balanceAuto(Command driveForwardOverChargeStationBlueCommand, 
            Command driveBackwardsOntoChargeStationBlueCommand, Command driveForwardOverChargeStationRedCommand, 
            Command driveBackwardsOntoChargeStationRedCommand, Command balanceChargeStation, ArmSubsystem arm) {

        return balanceAutoFirstHalf(driveForwardOverChargeStationBlueCommand, driveForwardOverChargeStationRedCommand, arm)
        .andThen(balanceAutoSecondHalf(driveBackwardsOntoChargeStationBlueCommand, driveBackwardsOntoChargeStationRedCommand, balanceChargeStation));
    }

    // Assumes robot is at a AprilTag
    // Might need to add calibration
    public static CommandBase placeConeAuto(ArmSubsystem arm, ClawGripSubsystem claw) {
        return Commands.runOnce(() -> {
            double[] newArmPosition = PositionConstants.TOP_RIGHT_POS; // or maybe top left pos?
            double[] currentArmPosition = arm.getCurrentCoordinates();
            arm.setTargetCoordinates(newArmPosition[0], newArmPosition[1], newArmPosition[2]);
            while (Math.abs(currentArmPosition[0] - newArmPosition[0]) > 1.0 || Math.abs(currentArmPosition[1] - newArmPosition[1]) > 1.0 || Math.abs(currentArmPosition[2] - newArmPosition[2]) > 1.0) {
                arm.goTowardIntendedCoordinates();
                currentArmPosition = arm.getCurrentCoordinates();
            }

            claw.setClawClosed(false); // open claw

            arm.setTargetCoordinates(ArmConstants.STARTING_X, ArmConstants.STARTING_Y, ArmConstants.STARTING_Z); // Go back to starting position
            currentArmPosition = arm.getCurrentCoordinates();
            while (Math.abs(currentArmPosition[0] - ArmConstants.STARTING_X) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.STARTING_Y) > 1.0 || Math.abs(currentArmPosition[2] - ArmConstants.STARTING_Z) > 1.0) {
                arm.goTowardIntendedCoordinates();
                currentArmPosition = arm.getCurrentCoordinates();
            }

            System.out.println("Auton finished");
        }, arm, claw);
    }

    // Position values on trajectories may need to be adjusted
    // Adjustments can be made later lol
    // Might need to add calibration 
    public static CommandBase doublePlacementAuto(ArmSubsystem arm, ClawGripSubsystem claw, Command driveBackwardsToCubeBlue, 
            Command driveForwardsToGridBlue, Command driveBackwardsToCubeRed, Command driveForwardsToGridRed) {

        if (blueTeam) {
            return placeConeAuto(arm, claw) // Drops pre-loaded cone onto top right pole
            .andThen(driveBackwardsToCubeBlue)
            .andThen(Commands.runOnce(() -> { // Grabs cube off floor
                claw.setClawClosed(false);
                double[] currentArmPosition = arm.getCurrentCoordinates();
                arm.setTargetCoordinates(-30, ArmConstants.PICK_UP_POSITION_Y, 0); // Supposed to be 30 inches in the opposite direction the robot is facing (towards middle of the field)
                // Might want to add aim assist code here somewhere
                while (Math.abs(currentArmPosition[0] - (-30)) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.PICK_UP_POSITION_Y) > 1.0 || Math.abs(currentArmPosition[2] - 0) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }
                claw.setClawClosed(true);
            })
            .andThen(Commands.runOnce(() -> { // Returns claw to starting position
                arm.setTargetCoordinates(ArmConstants.STARTING_X, ArmConstants.STARTING_Y, ArmConstants.STARTING_Z); // Go back to starting position
                double[] currentArmPosition = arm.getCurrentCoordinates();
                while (Math.abs(currentArmPosition[0] - ArmConstants.STARTING_X) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.STARTING_Y) > 1.0 || Math.abs(currentArmPosition[2] - ArmConstants.STARTING_Z) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }
            }))
            .alongWith(driveForwardsToGridBlue)
            .andThen(Commands.runOnce(() -> { // Drops cube onto center top position on grid
                double[] newArmPosition = PositionConstants.TOP_CENTER_POS; 
                double[] currentArmPosition = arm.getCurrentCoordinates();
                arm.setTargetCoordinates(newArmPosition[0], newArmPosition[1], newArmPosition[2]);
                while (Math.abs(currentArmPosition[0] - newArmPosition[0]) > 1.0 || Math.abs(currentArmPosition[1] - newArmPosition[1]) > 1.0 || Math.abs(currentArmPosition[2] - newArmPosition[2]) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }

                claw.setClawClosed(false); // open claw

                arm.setTargetCoordinates(ArmConstants.STARTING_X, ArmConstants.STARTING_Y, ArmConstants.STARTING_Z); // Go back to starting position
                currentArmPosition = arm.getCurrentCoordinates();
                while (Math.abs(currentArmPosition[0] - ArmConstants.STARTING_X) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.STARTING_Y) > 1.0 || Math.abs(currentArmPosition[2] - ArmConstants.STARTING_Z) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }

                System.out.println("Auton finished");
            })));
        } else {
            return placeConeAuto(arm, claw) // Drops pre-loaded cone onto top right pole
            .andThen(driveBackwardsToCubeRed)
            .andThen(Commands.runOnce(() -> { // Grabs cube off floor
                claw.setClawClosed(false);
                double[] currentArmPosition = arm.getCurrentCoordinates();
                arm.setTargetCoordinates(-30, ArmConstants.PICK_UP_POSITION_Y, 0); // Supposed to be 30 inches in the opposite direction the robot is facing (towards middle of the field)
                // Might want to add aim assist code here somewhere
                while (Math.abs(currentArmPosition[0] - (-30)) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.PICK_UP_POSITION_Y) > 1.0 || Math.abs(currentArmPosition[2] - 0) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }
                claw.setClawClosed(true);
            })
            .andThen(Commands.runOnce(() -> { // Returns claw to starting position
                arm.setTargetCoordinates(ArmConstants.STARTING_X, ArmConstants.STARTING_Y, ArmConstants.STARTING_Z); // Go back to starting position
                double[] currentArmPosition = arm.getCurrentCoordinates();
                while (Math.abs(currentArmPosition[0] - ArmConstants.STARTING_X) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.STARTING_Y) > 1.0 || Math.abs(currentArmPosition[2] - ArmConstants.STARTING_Z) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }
            }))
            .alongWith(driveForwardsToGridRed)
            .andThen(Commands.runOnce(() -> { // Drops cube onto center top position on grid
                double[] newArmPosition = PositionConstants.TOP_CENTER_POS; 
                double[] currentArmPosition = arm.getCurrentCoordinates();
                arm.setTargetCoordinates(newArmPosition[0], newArmPosition[1], newArmPosition[2]);
                while (Math.abs(currentArmPosition[0] - newArmPosition[0]) > 1.0 || Math.abs(currentArmPosition[1] - newArmPosition[1]) > 1.0 || Math.abs(currentArmPosition[2] - newArmPosition[2]) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }

                claw.setClawClosed(false); // open claw

                arm.setTargetCoordinates(ArmConstants.STARTING_X, ArmConstants.STARTING_Y, ArmConstants.STARTING_Z); // Go back to starting position
                currentArmPosition = arm.getCurrentCoordinates();
                while (Math.abs(currentArmPosition[0] - ArmConstants.STARTING_X) > 1.0 || Math.abs(currentArmPosition[1] - ArmConstants.STARTING_Y) > 1.0 || Math.abs(currentArmPosition[2] - ArmConstants.STARTING_Z) > 1.0) {
                    arm.goTowardIntendedCoordinates();
                    currentArmPosition = arm.getCurrentCoordinates();
                }

                System.out.println("Auton finished");
            })));
        }
    }

    // Might need to add calibration
    public static CommandBase placeConeThenBalanceAuto(Command driveForwardOverChargeStationBlueCommand, 
    Command driveBackwardsOntoChargeStationBlueCommand, Command driveForwardOverChargeStationRedCommand, 
    Command driveBackwardsOntoChargeStationRedCommand, Command balanceChargeStation, ArmSubsystem arm, ClawGripSubsystem claw) {

        return balanceAutoFirstHalf(driveForwardOverChargeStationBlueCommand, driveForwardOverChargeStationRedCommand, arm)
        .andThen(placeConeAuto(arm, claw))
        .andThen(balanceAutoSecondHalf(driveBackwardsOntoChargeStationBlueCommand, driveBackwardsOntoChargeStationRedCommand, balanceChargeStation));
        
    }

    private Autos() {
        throw new UnsupportedOperationException("Autos is a utility class and cannot be instantiated!");
    }
}
