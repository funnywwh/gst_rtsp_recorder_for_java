package rtsp;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RtspRecorder {
	public static void KillINT(long pid) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("kill", "-s", "INT", "" + pid);
		processBuilder.start();
	}

	public static long ProcessId(Process p) {
		long pid = -1;
		Field field = null;
		try {
			Class<?> clazz = Class.forName("java.lang.UNIXProcess");
			field = clazz.getDeclaredField("pid");
			field.setAccessible(true);
			pid = (Integer) field.get(p);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return pid;
	}

	public static long StartRecord(String ip,String mp4path){
		long pid = 0;
		try {
		
				ProcessBuilder processBuilder = new ProcessBuilder();
				String url = String.format("location=rtsp://admin:admin@%s/video0",ip);
				//String url = String.format("location=rtsp://admin:admin@%s/video0","192.168.1.2");
				String patharg = String.format("location=%s",mp4path);
				ArrayList<String> args = new ArrayList<String>();
				args.add("gst-launch-1.0");
				args.add("rtspsrc");
				args.add(url);
				args.add("!");
				args.add("rtpjitterbuffer");
				args.add("!");
				args.add("rtph264depay");
				args.add("!");
				args.add("h264parse");
				args.add("!");
				args.add("mp4mux");
				args.add("!");
				args.add("filesink");
				args.add(patharg);
				args.add("-e");
				processBuilder.command(args);
				Process process = processBuilder.start();
				pid = ProcessId(process);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		return pid;
	}

	public static void StopRecord(long pid) {
		if (pid > 0) {
			try {
				KillINT(pid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		long pid = StartRecord("192.168.1.2", "/home/winger/1.mp4");
		try {
			Thread.sleep(10 * 1000);
			StopRecord(pid);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
};