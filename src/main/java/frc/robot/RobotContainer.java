// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.PositionConstants;
import frc.robot.Constants.OperatorConstants.ControllerConstants;
import frc.robot.commands.armcommands.AimAssistCommand;
import frc.robot.commands.armcommands.ArmControlCommand;
import frc.robot.commands.armcommands.ArmTestCommand;
import frc.robot.commands.armcommands.CalibrateArmPivotsCommand;
import frc.robot.commands.armcommands.GoTowardsCoordinatesCommandAuto;
import frc.robot.commands.armcommands.GoTowardsCoordinatesCommandTeleop;
import frc.robot.commands.armcommands.PoseAimArmCommand;
import frc.robot.commands.autocommands.Autos;
import frc.robot.commands.clawcommands.ClawOpenandCloseCommand;
import frc.robot.commands.clawcommands.ClawRotateCommand;
import frc.robot.commands.drivecommands.BalanceChargeStationCommand;
import frc.robot.commands.drivecommands.ManualDriveCommand;
import frc.robot.controllers.FlightJoystick;
import frc.robot.controllers.XboxController;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClawGripSubsystem;
import frc.robot.subsystems.ClawRotationSubsystem;
import frc.robot.subsystems.DriveTrainSubsystem;
import frc.robot.subsystems.staticsubsystems.LimeLight;
import frc.robot.subsystems.staticsubsystems.RobotGyro;
import frc.robot.wrappers.TrajectoryReader;

import frc.robot.util.CommandGenerator;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */

public class RobotContainer {
    public static boolean inTeleop = false;

    // Replace with CommandPS4Controller or CommandJoystick if needed
    public final FlightJoystick driverController = new FlightJoystick(new CommandJoystick(OperatorConstants.RIGHT_JOYSTICK_PORT));
    // public final FlightJoystick armController = new FlightJoystick(new CommandJoystick(OperatorConstants.LEFT_JOYSTICK_PORT));
    public final XboxController xboxController = new XboxController(new CommandXboxController(OperatorConstants.XBOX_CONTROLLER_PORT));

    // The robot's subsystems and commands are defined here...
    public final DriveTrainSubsystem driveTrain = new DriveTrainSubsystem(driverController);
    public final ArmSubsystem arm = new ArmSubsystem();
    public final ClawGripSubsystem clawGrip = new ClawGripSubsystem();
    public final ClawRotationSubsystem clawRotation = new ClawRotationSubsystem();

    public final TrajectoryReader trajectoryReader = new TrajectoryReader("robogui", "trajectory");

    public final ManualDriveCommand manualDrive = new ManualDriveCommand(driveTrain, driverController);

    
    // Command suppliers
    public final Supplier<ArmTestCommand>                  testArmControl          = () -> new ArmTestCommand(arm, xboxController);
    public final Supplier<ArmControlCommand>               armControl              = () -> new ArmControlCommand(arm, clawGrip, xboxController);
    public final Supplier<AimAssistCommand>                aimAssist               = () -> new AimAssistCommand(arm);

    public final Supplier<ClawOpenandCloseCommand>         clawOpenandCloseCommand = () -> new ClawOpenandCloseCommand(clawGrip, xboxController);
    public final Supplier<ClawRotateCommand>               clawRotateCommand       = () -> new ClawRotateCommand(clawRotation, xboxController);

    public final Supplier<GoTowardsCoordinatesCommandAuto> goToTopCenter           = () -> new GoTowardsCoordinatesCommandAuto(arm,  PositionConstants.TOP_CENTER_POS, 0.4, 0.4);
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToCenterMiddle        = () -> new GoTowardsCoordinatesCommandAuto(arm,  PositionConstants.CENTER_MIDDLE_POS, 0.4, 0.4);
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToCenterRight         = () -> new GoTowardsCoordinatesCommandAuto(arm,   PositionConstants.CENTER_RIGHT_POS, 0.4, 0.4);
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToStartingPos         = () -> new GoTowardsCoordinatesCommandAuto(arm,  ArmConstants.STARTING_COORDS , 0.2, 0.4);
    public final Supplier<BalanceChargeStationCommand>     balanceCommand          = () -> new BalanceChargeStationCommand(driveTrain);
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToPickupPosX30        = () -> new GoTowardsCoordinatesCommandAuto(arm, new double[] {-30, ArmConstants.PICK_UP_POSITION_Y, 0}, 0.4, 0.4);
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToPickupPosX35        = () -> new GoTowardsCoordinatesCommandAuto(arm, new double[] {-35, ArmConstants.PICK_UP_POSITION_Y, 0}, 0.4, 0.4); 
    public final Supplier<GoTowardsCoordinatesCommandAuto> goToAbovePickupPos      = () -> new GoTowardsCoordinatesCommandAuto(arm, new double[] {-35, ArmConstants.PICK_UP_POSITION_Y + 10, 0}, 0.4, 0.4); 

    public final Supplier<GoTowardsCoordinatesCommandAuto> goTowardsPickupCommand  = () -> new GoTowardsCoordinatesCommandAuto(arm, new double[] {-30, ArmConstants.PICK_UP_POSITION_Y, 0}, 0.4, 0.4); // Implement later during downtime


    public CommandGenerator driveForwardOverChargeStationBlue     = new CommandGenerator("DriveForwardOverChargeStationBlue");
    public CommandGenerator driveBackwardsOntoChargeStationBlue   = new CommandGenerator("DriveBackwardsOntoChargeStationBlue");
    public CommandGenerator driveForwardOverChargeStationRed      = new CommandGenerator("DriveForwardOverChargeStationRed");
    public CommandGenerator driveBackwardsOntoChargeStationRed    = new CommandGenerator("DriveBackwardsOntoChargeStationRed");
    public CommandGenerator driveBackwardsToConeBlue              = new CommandGenerator("DriveBackwardsToConeBlue");
    public CommandGenerator driveForwardsToGridBlue               = new CommandGenerator("DriveForwardsToGridBlue");
    public CommandGenerator driveBackwardsToConeRed               = new CommandGenerator("DriveBackwardsToConeRed");
    public CommandGenerator driveForwardsToGridRed                = new CommandGenerator("DriveForwardsToGridRed");
    public CommandGenerator driveBackwardsOntoChargeStationDPRed  = new CommandGenerator("DriveBackwardsOntoChargeStationDPRed");
    public CommandGenerator driveBackwardsOntoChargeStationDPBlue = new CommandGenerator("DriveBackwardsOntoChargeStationDPBlue");
    public CommandGenerator moveOneMeter                          = new CommandGenerator("MoveOneMeter");
    public CommandGenerator driveBackwardsToCubeBlue              = new CommandGenerator("DriveBackwardsToCubeBlue");
    public CommandGenerator driveBackwardsToCubeRed               = new CommandGenerator("DriveBackwardsToCubeRed");


    private Command defaultAuto = Autos.defaultAuto(/* pass in parameters */); // placeholder, pass in subsystems or commands if needed
    private Command testAuto = Autos.exampleAuto(/*pass in parameters */);   // placeholder, pass in subsystems or commands if needed
    private Command taxiAuto;
    private Command taxiForBalanceAuto;
    private Command balanceAuto;
    private Command placeCubeThenTaxiAuto;
    private Command placeCubeThenBalanceAuto;
    private Command doublePlacementAuto;
    private Command balanceAutoPW; 
    private Command doublePlacementAutoPW;
    private Command placeCubeThenBalanceAutoPW;
    private Command doublePlacementThenBalanceAutoPW;
    
    private Command m_autonomousCommand;
    private final SendableChooser<Command> m_chooser = new SendableChooser<>();

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();

        // Poke the static classes so their static initializers are run at startup.
        LimeLight.poke();
        RobotGyro.poke();
    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
     * predicate, or via the named factories in {@link
     * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
     * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
     * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
     * joysticks}.
     */
    private void configureBindings() {
        // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
        // new Trigger(exampleSubsystem::exampleCondition)
        //    .onTrue(new ManualDrive(exampleSubsystem));

        // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
        // cancelling on release.
        // driverController.b().whileTrue(exampleSubsystem.exampleMethodCommand());

        // driverController.joystick.button(ControllerConstants.RUN_GUI_TRAJECTORY_BUTTON_NUMBER).onTrue(this.driveTrain.generateRamseteCommand(this.trajectoryReader.currentTrajectory));
       
        // armController.joystick.button(ControllerConstants.CLAW_ROTATE_RIGHT_BUTTON_NUMBER).whileTrue(clawRotation.rotateClawRight());
        // armController.joystick.button(ControllerConstants.CLAW_ROTATE_LEFT_BUTTON_NUMBER).whileTrue(clawRotation.rotateClawLeft());
        // armController.joystick.button(ControllerConstants.AUTO_ROTATE_BUTTON_NUMBER).whileTrue(clawRotation.autoRotate());
        // xboxController.controller.button(ControllerConstants.CALIBRATE_ARM_BUTTON_NUMBER).onTrue(arm.calibrateArm());
        xboxController.controller.button(ControllerConstants.CALIBRATE_ARM_BUTTON_NUMBER).onTrue(new CalibrateArmPivotsCommand(arm, xboxController));
        driverController.joystick.button(ControllerConstants.BALANCE_CHARGE_STATION_BUTTON_NUMBER).whileTrue(new BalanceChargeStationCommand(driveTrain));

        driverController.joystick.button(7).onTrue(new GoTowardsCoordinatesCommandTeleop(arm, new double[] {-35, ArmConstants.PICK_UP_POSITION_Y, 0}, xboxController, 0.2, 0.2, false));
        //driverController.joystick.button(1).whileTrue(new PoseAimArmCommand(arm, driveTrain, new Translation3d(20,60,0)));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        // var traj = this.driveTrain.generateTrajectory(new Pose2d(0, 0, new Rotation2d()), List.of(), new Pose2d(1, 0, new Rotation2d()), false);
        // System.out.println(traj);
        // return this.driveTrain.generateRamseteCommand(traj);
        return m_chooser.getSelected();
    }

    public void onRobotInit() {
        /**
         * Initialize Pathweaver trajectories/commands here
         */

        CommandGenerator.initializeAll(driveTrain); // ez

        // Initialize autonomous commands here

        taxiAuto                         = Autos.taxiAuto(this);
        taxiForBalanceAuto               = Autos.taxiForBalanceAuto(this);
        placeCubeThenTaxiAuto            = Autos.placeCubeThenTaxiAuto(this);
        balanceAuto                      = Autos.balanceAuto(this);
        placeCubeThenBalanceAuto         = Autos.placeCubeThenBalanceAuto(this);
        doublePlacementAuto              = Autos.doublePlacementAuto(this);

        // All below autos use Pathweaver trajectories and probably don't work right now
        balanceAutoPW                      = Autos.balanceAutoPW(this);
        doublePlacementAutoPW              = Autos.doublePlacementAutoPW(this);
        placeCubeThenBalanceAutoPW         = Autos.placeCubeThenBalanceAutoPW(this);
        doublePlacementThenBalanceAutoPW   = Autos.doublePlacementThenBalanceAutoPW(this);


        // Adds autonomous options to dashboard
        m_chooser.setDefaultOption("Default Auto", defaultAuto);
        m_chooser.addOption("Test Auto", testAuto);
        m_chooser.addOption("Taxi Auto", taxiAuto);
        m_chooser.addOption("Taxi for Balance Auto", taxiForBalanceAuto);
        m_chooser.addOption("Balance Auto", balanceAuto);
        m_chooser.addOption("Place Cube then Taxi Auto", placeCubeThenTaxiAuto);
        m_chooser.addOption("Place Cube then Balance Auto", placeCubeThenBalanceAuto);
        m_chooser.addOption("Double Placement Auto", doublePlacementAuto);

        // These autons use Pathweaver
        m_chooser.addOption("Balance Auto (PathWeaver)", balanceAutoPW);
        m_chooser.addOption("Double Placement Auto (PathWeaver)", doublePlacementAutoPW);
        m_chooser.addOption("Place Cube then Balance Auto (PathWeaver)", placeCubeThenBalanceAutoPW);
        m_chooser.addOption("Double Placement Then Balance Auto (PathWeaver)", doublePlacementThenBalanceAutoPW);
        m_chooser.addOption("Move one meter test (PathWeaver", moveOneMeter.get());

        SmartDashboard.putData("Auto choices", m_chooser);
}

    public void onAutonInit() {
        inTeleop = false;

        m_autonomousCommand = this.getAutonomousCommand();

        System.out.println("Autonomous initiated");
        // schedule the autonomous command (example)
        if (m_autonomousCommand != null) {
            System.out.println("Begin autonomous scheduling");
            m_autonomousCommand.schedule();
            System.out.println("Autonomous scheduled");
        }
    }

    public void onTeleopInit() {
        inTeleop = true;
        this.arm.setPIDControlState(false);
        
        this.driveTrain.setDefaultCommand(this.manualDrive);
        this.arm.setDefaultCommand(this.armControl.get());
        this.clawGrip.setDefaultCommand(this.clawOpenandCloseCommand.get());
        this.clawRotation.setDefaultCommand(this.clawRotateCommand.get());
    }
}
