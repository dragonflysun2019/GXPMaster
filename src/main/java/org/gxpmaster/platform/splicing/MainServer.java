package org.gxpmaster.platform.splicing;

public class MainServer {
    public static void main(String[] args) {
        try {
            IBaseServer bs = (IBaseServer) Class.forName(PropertyMgr.get("servermode")).newInstance();
            bs.start();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
