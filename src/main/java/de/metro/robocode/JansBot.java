package de.metro.robocode;

import robocode.*;

import java.awt.*;

public class JansBot extends AdvancedRobot {
    int moveDirection = 1;

    String currentFocussedEnemysName = "";
    double currentFocussedEnemysDistance = Double.POSITIVE_INFINITY;

    double heuristicFactor = 1.5;
    double heuristicDistance = 140;

    @Override
    public void run() {
        setAdjustRadarForRobotTurn(true);//keep the radar still while we turn
        setBodyColor(Color.black);
        setGunColor(Color.gray);
        setRadarColor(Color.green);
        setScanColor(Color.white);
        setBulletColor(Color.red);
        setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
        turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right

        while(true) {
            if( currentFocussedEnemysName == "" ) { //move if you have not enemy in focus
                setTurnRightRadians( -Math.PI / 8.0 );
                setAhead( 20.0 );
                execute();
            }
        }

    }

    public void onScannedRobot(ScannedRobotEvent e) {

        if( e.getName() != currentFocussedEnemysName ) {
            if ( e.getDistance( ) < currentFocussedEnemysDistance ) { //focus on this enemy!
                currentFocussedEnemysName = e.getName( );
            } else {
                return; //don't do anything
            }
        }

        currentFocussedEnemysDistance = e.getDistance( );

        if ( Math.random( ) > .9 ) {
            setMaxVelocity( ( 12 * Math.random( ) ) + 12 );//randomly change speed
        }

        //find out where other robot is
        double absBearing = e.getBearingRadians( ) + getHeadingRadians( );//enemies absolute bearing
        double eX = getX() + currentFocussedEnemysDistance * Math.sin( absBearing );
        double eY = getY() + currentFocussedEnemysDistance * Math.cos( absBearing );

        //find out where the robot will be soon
        double eXp = eX + e.getVelocity() * Math.sin( e.getHeadingRadians() );
        double eYp = eY + e.getVelocity() * Math.cos( e.getHeadingRadians() );


        //calculate radial velocity of enemy with respect to me
        double radialVelocity = (e.getVelocity( ) / e.getDistance()) / 2.0 / Math.PI * Math.sin( e.getHeadingRadians( ) - getHeadingRadians() - e.getBearingRadians() );

        setTurnGunRightRadians( betweenMinusPiAndPi( e.getBearingRadians()  - getGunHeadingRadians() + getHeadingRadians() + radialVelocity * heuristicFactor ) );

        if( e.getDistance() < heuristicDistance ) { // better turn away
            setTurnRightRadians( betweenMinusPiAndPi( Math.PI / 2.0 + e.getBearingRadians() ) );
        }
        else { //rather move towards the enemy
            setTurnRightRadians( betweenMinusPiAndPi( e.getBearingRadians() + radialVelocity * heuristicFactor ) );
        }


        setAhead( ( e.getDistance( ) - heuristicDistance + 10.0 ) * moveDirection );//move forward

        setFire(3);

    }
    private double betweenMinusPiAndPi(double angle) {
        return robocode.util.Utils.normalRelativeAngle(angle);
    }

    public void onHitWall(HitWallEvent e){
        setTurnRightRadians( Math.PI / 1.5 );
    }

    public void onRobotDeath(RobotDeathEvent e) {
        if( currentFocussedEnemysName == e.getName() ) {
            currentFocussedEnemysName = "";
            currentFocussedEnemysDistance = Double.POSITIVE_INFINITY;
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {

        setTurnRightRadians( Math.PI / 2.0 + e.getBearingRadians() );
        setAhead( 20.0 * moveDirection );
        execute();

    }

}
