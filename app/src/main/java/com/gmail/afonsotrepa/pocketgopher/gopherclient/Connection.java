package com.gmail.afonsotrepa.pocketgopher.gopherclient;


import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Connection {
    private Socket socket;
	private PrintWriter os; //output stream
	private BufferedReader is; //input stream

	/**
	 * Opens a connection with the server
	 *
	 * @param server ip or DNS address of the server
	 * @param port port the server and the client listen to
	 */
	Connection(String server, Integer port) throws IOException{
	    socket =  new Socket(server, port);

	    os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
	    is = new BufferedReader((new InputStreamReader((socket.getInputStream()))));
	}
	
	/**
	 * Sends/writes a string to the server
	 * @param message string to send to the server
	 */
	private void write(String message) {
        os.println(message);
        os.flush();
	}

	/**
	 * Receives/reads from the server
     * @return a message sent by the server
	 */
	private String read() {
        StringBuilder sb = new StringBuilder();
        String line;
		try {

            //read until the end of the message (EOF or ".")
			while ((line = is.readLine()) != null && !line.equals(".")){
				sb.append(line).append('\n');
			}
		}
		catch (IOException e){
            e.printStackTrace();
		}
		return sb.toString();
	}


	/**
	 * Sends the selector to the server and returns the response (excpects a directory/menu)
	 * @param selector selector (see RFC 1436)
	 * @return the response from the server (as GopherLine objects)
	 */
	List<GopherLine> getMenu(String selector) {
		this.write(selector); //send the selector
		String lines[] = this.read().split("\n"); //read the response by the server

		List<GopherLine> response = new ArrayList<>();

		for (String line : lines) {
			//skip line if empty
			if (line.equals(""))
				continue;

            String[] linesplit = line.split("\t");
			switch (line.charAt(0)) {
				case '0': //text file
				    response.add(new TextFileGopherLine(
				            linesplit[0].substring(1), //remove the type tag
                            linesplit[1],
                            linesplit[2],
                            Integer.parseInt(linesplit[3])));
				    break;

                case '1': //menu/directory
                    response.add(
                            new MenuGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
					break;

                case '7': //Search engine or CGI script
                    response.add(
                            new SearchGopherLine(
                                    linesplit[0].substring(1), //remove the type tag
                                    linesplit[1],
                                    linesplit[2],
                                    Integer.parseInt(linesplit[3])));
                    break;

                case 'i': //Informational text (not in the protocol but common)
                    response.add(new TextGopherLine(linesplit[0].substring(1), linesplit[2]));
                    break;

				case 'g': //gif (temporary)
				case 'I': //Image
					response.add(
							new ImageGopherLine(
									linesplit[0].substring(1), //remove the type tag
									linesplit[1],
									linesplit[2],
									Integer.parseInt(linesplit[3])));
					break;

				default:
					//using substring(1) will crash sometimes (no idea why)
					response.add(new UnknownGopherLine(linesplit[0].substring(0), line.charAt(0)));
			}
		}

		return response;
	}

    /**
     * Sends the selector to the server and returns the response (expects a text file)
     * @param selector selector (see RFC 1436)
     * @return the response from the server (as strings)
     */
    List<String> getTextFile(String selector) {
        this.write(selector); //send the selector
        return Arrays.asList(this.read().split("\n")); //read the response by the server
    }

    Drawable getImage(String selector) throws IOException {
		this.write(selector); //send the selector
        return Drawable.createFromStream(socket.getInputStream(), "src");

	}

}
