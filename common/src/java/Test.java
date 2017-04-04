/*
 * Copyright Jurong Port Pte Ltd
 * Created on Dec 6, 2007
 */
import java.net.*;
public class Test {

	public static void main(String[] args) {
		System.out.println("hello");
		try {
			InetAddress a = InetAddress.getLocalHost();
			System.out.println(a.getHostName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
