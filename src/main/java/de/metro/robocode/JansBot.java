package de.metro.robocode;

import robocode.*;

import java.awt.*;

public class JansBot extends AdvancedRobot {
    int moveDirection = 1;

    String currentFocussedEnemysName = "";
    double currentFocussedEnemysDistance = Double.POSITIVE_INFINITY;

    double heuristicFactor = 1.0;

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




        //find out where other robot is
        double absBearing = e.getBearingRadians( ) + getHeadingRadians( );//enemies absolute bearing
        double eX = getX() + currentFocussedEnemysDistance * Math.sin( absBearing );
        double eY = getY() + currentFocussedEnemysDistance * Math.cos( absBearing );

        //find out where the robot will be soon
        double eXp = eX + e.getVelocity() * Math.sin( e.getHeadingRadians() );
        double eYp = eY + e.getVelocity() * Math.cos( e.getHeadingRadians() );


        //calculate radial velocity of enemy with respect to me
        double radialVelocity = (e.getVelocity( ) / e.getDistance()) / 2.0 / Math.PI * Math.sin( e.getHeadingRadians( ) - getHeadingRadians() - e.getBearingRadians() );
        setTurnGunRightRadians( e.getBearingRadians()  - getGunHeadingRadians() + getHeadingRadians() + radialVelocity * heuristicFactor );

        if( e.getDistance() < 150 ) { // better turn away
            setTurnRightRadians( Math.PI / 2.0 + e.getBearingRadians() );
        }
        else { //rather move towards the enemy
            setTurnRightRadians( e.getBearingRadians() + radialVelocity * heuristicFactor );
        }


        setAhead( ( e.getDistance( ) - 140 ) * moveDirection );//move forward

        setFire(3);

        /*
        double latVel = e.getVelocity( ) * Math.sin( e.getHeadingRadians( ) - absBearing );
        double gunTurnAmt;//amount to turn our gun
        setTurnRadarLeftRadians( getRadarTurnRemainingRadians( ) );//lock on the radar
        if ( Math.random( ) > .9 ) {
            setMaxVelocity( ( 12 * Math.random( ) ) + 12 );//randomly change speed
        }
        if ( e.getDistance( ) > 150 ) {//if distance is greater than 150
            gunTurnAmt = robocode.util.Utils.normalRelativeAngle( absBearing - getGunHeadingRadians( ) + latVel / 22 );//amount to turn our gun, lead just a little bit
            setTurnGunRightRadians( gunTurnAmt ); //turn our gun
            setTurnRightRadians( robocode.util.Utils.normalRelativeAngle( absBearing - getHeadingRadians( ) + latVel / getVelocity( ) ) );//drive towards the enemies predicted future location
            setAhead( ( e.getDistance( ) - 140 ) * moveDirection );//move forward
            setFire( 3 );//fire
        } else {//if we are close enough...
            gunTurnAmt = robocode.util.Utils.normalRelativeAngle( absBearing - getGunHeadingRadians( ) + latVel / 15 );//amount to turn our gun, lead just a little bit
            setTurnGunRightRadians( gunTurnAmt );//turn our gun
            setTurnLeft( -90 - e.getBearing( ) ); //turn perpendicular to the enemy
            setAhead( ( e.getDistance( ) - 140 ) * moveDirection );//move forward
            setFire( 3 );//fire
        }
        */
    }
    public void onHitWall(HitWallEvent e){
        moveDirection=-moveDirection;//reverse direction upon hitting a wall
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

}
