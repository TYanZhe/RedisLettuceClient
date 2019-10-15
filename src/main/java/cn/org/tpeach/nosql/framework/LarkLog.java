package cn.org.tpeach.nosql.framework;

import lombok.Getter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Observable;

public class LarkLog extends Observable {
    @Getter
    private LogMsg logMsg;

    public void info(LocalDateTime receiveTime ,String msg, Object... args){
        info(receiveTime,msg,null,args);
    }
    public void info(LocalDateTime receiveTime ,String msg, Color color, Object... args){

        this.logMsg = new LogMsg(String.format(msg,args),receiveTime);
        setChanged();
        if(color != null){
            notifyObservers(color);
        }else{
            notifyObservers( );
        }

    }
    public void error(LocalDateTime receiveTime ,String msg, Throwable e){
        if(e !=null){
            info(receiveTime,msg+" < "+e.getMessage(),Color.RED);
        }else{
            info(receiveTime,msg,Color.RED);
        }

    }
    public void error(LocalDateTime receiveTime ,String msg ){
        error(receiveTime,msg,null);
    }

    public void sendInfo(LocalDateTime receiveTime ,String server,String msg,  Object... args){
        info(receiveTime,"[Server "+server+"] > [Send Command] : "+msg,Color.ORANGE.darker() ,args );
    }
    public void sendInfo( String server,String msg,  Object... args){
        sendInfo(LocalDateTime.now(),server,msg,args);
    }
    public void receivedInfo( String server,String msg,  Object... args){
        receivedInfo(LocalDateTime.now(),server,msg,args);
    }
    public void receivedInfo(LocalDateTime receiveTime ,String server,String msg, Object... args){
        info(receiveTime,"[Server "+server+"] < [Response Received] : "+msg ,args );
    }

    public void receivedError( String server,String msg, Throwable e){
        receivedError(LocalDateTime.now(),server,msg,e);
    }
    public void receivedError(LocalDateTime receiveTime ,String server,String msg, Throwable e){
        if(e != null){
            info(receiveTime,"[Server %s] < [Response Received] : %s %s",Color.RED,server,msg,e.getMessage());
        }else{
            info(receiveTime ,"[Server %s] < [Response Received] : %s ",Color.RED,server,msg );
        }

    }
}
