package atom;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.util.*;

public class Atom extends RateControlRobot {
    /**
     * run: executado quando o round for iniciado
     */
    public void run() {
	    // cor do rapaz
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.white);
		setBulletColor(Color.blue);

	
        // Definindo posição inicial do robo
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Girando maluco 
        while (true) {
            setVelocityRate(6);
            setTurnRateRadians(0);
            execute();
            turnRadarRight(360);
        }
    }
    /**
     * onScannedRobot: Executado quando o radar encontra um robo.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        double potenciaDoTiro = Math.min(2.0, getEnergy());
        double distancia = getHeadingRadians() + e.getBearingRadians();
        double inimigoX = getX() + e.getDistance() * Math.sin(distancia);
        double inimigoY = getY() + e.getDistance() * Math.cos(distancia);
        double posicaoDoInimigo = e.getHeadingRadians();
        double velocidadeDoInimigo = e.getVelocity();
        double altDoCampDeBatalha = getBattleFieldHeight(),larDoCampDeBatalha = getBattleFieldWidth();
        double previsaoX = inimigoX, previsaoY = inimigoY;
        
        previsaoX += Math.sin(posicaoDoInimigo) * velocidadeDoInimigo;
        previsaoY += Math.cos(posicaoDoInimigo) * velocidadeDoInimigo;
        if (previsaoX < 18.0 || previsaoY < 18.0 || previsaoX > larDoCampDeBatalha - 18.0 || previsaoY > altDoCampDeBatalha - 18.0) {
            previsaoX = Math.min(Math.max(18.0, previsaoX), larDoCampDeBatalha - 18.0);
            previsaoY = Math.min(Math.max(18.0, previsaoY), altDoCampDeBatalha - 18.0);
        }
        
        double anguloAbsoluto = Utils.normalAbsoluteAngle(
            Math.atan2(
                previsaoX - getX(), previsaoY - getY()
            )
        );
        
        setTurnRightRadians(distancia / 2 * - 1 - getRadarHeadingRadians());
        setTurnRadarRightRadians(Utils.normalRelativeAngle(distancia - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(anguloAbsoluto - getGunHeadingRadians()));
        fire(potenciaDoTiro);
        
        if (getVelocityRate() > 0){
            setVelocityRate(getVelocityRate() + 1);
        } 
        else {
            setVelocityRate(- 1);
        }

        if (getVelocityRate() > 0 && ((getTurnRate() < 0 && distancia > 0) || (getTurnRate() > 0 && distancia < 0))) {
            setTurnRate(getTurnRate() * -1);
        }
    }
    /**
     * onHitByBullet: É executado quando o robô leva um bala.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        double giroDoRadar = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getRadarHeading());
        setTurnRadarRight(giroDoRadar);
        setTurnLeft(-3);
        setTurnRate(3);
        setVelocityRate(-1 * getVelocityRate());
    }
    /**
     * onHitWall: É executado quando o robô colide com a parede.
     */
    public void onHitWall(HitWallEvent e) {
        setVelocityRate(-1 * getVelocityRate());
        setTurnRate(getTurnRate() + 2);
        execute();
    }
    /**
     * onHitRobot: É executado quando o robô bate em outro robô.
     */
    public void onHitRobot(HitRobotEvent e) {
        double giroDoCanhao = normalRelativeAngleDegrees(e.getBearing() + getHeading() - getGunHeading());
        turnGunRight(giroDoCanhao);
        setFire(3);
        setVelocityRate(getVelocity() + 3);
        execute();
    }
    /**
     * onWin: É executado quando o robô ganha o round.
     */
  
}
