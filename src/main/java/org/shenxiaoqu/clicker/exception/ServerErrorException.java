package org.shenxiaoqu.clicker.exception;

/**
 * Created with IntelliJ IDEA.
 * User: longhengyu
 * Date: 12-12-7
 * Time: 下午10:20
 * To change this template use File | Settings | File Templates.
 */
public class ServerErrorException extends RuntimeException {

    String message;

    public ServerErrorException(String msg) {
        this.setMessage(msg);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
