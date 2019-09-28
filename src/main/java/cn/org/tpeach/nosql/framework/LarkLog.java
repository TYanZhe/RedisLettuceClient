package cn.org.tpeach.nosql.framework;

import lombok.Getter;

import java.awt.*;
import java.util.Observable;

public class LarkLog extends Observable {
    @Getter
    private LogMsg logMsg;

    public void info(String msg, Object... args){
        info(msg,null,args);
    }
    public void info(String msg, Color color, Object... args){

        this.logMsg = new LogMsg(String.format(msg,args));
        setChanged();
        if(color != null){
            notifyObservers(color);
        }else{
            notifyObservers( );
        }

    }
    public void error(String msg, Throwable e){
        if(e !=null){
            info(msg+" > "+e.getMessage(),Color.RED);
        }else{
            info(msg,Color.RED);
        }

    }
    public void error(String msg ){
        error(msg,null);
    }

    public void sendInfo(String server,String msg,  Object... args){
        info("[Server "+server+"] > [Send Command] : "+msg,Color.ORANGE.darker() ,args );
    }
    public void receivedInfo(String server,String msg, Object... args){
        info("[Server "+server+"] < [Response Received] : "+msg ,args );
    }
    public void receivedError(String server,String msg, Throwable e){
        if(e != null){
            info("[Server %s] < [Response Received] : %s > %s",Color.RED,server,msg,e.getMessage());
        }else{
            info("[Server %s] < [Response Received] : %s ",Color.RED,server,msg );
        }

    }
}
