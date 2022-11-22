/**
 *  HybridServer
 *  Copyright (C) 2022 Miguel Reboiro-Jato
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

//Nunca dejes para mañana lo que puedas hacer pasado mañana (Mark Twain)

public class Launcher {
	public static void main(String[] args) throws IOException {

		// Si pudieras patear en el trasero al responsable de casi todos tus problemas,
		// no podrías sentarte por un mes (Theodore Roosevelt)

		Properties properties = new Properties();
		String propertiesPath;

		if (args.length == 1) {
			propertiesPath = args[0];

			// read and load file
			File propertiesFile = new File(propertiesPath);

			propertiesFile.createNewFile();
			try (Reader propertiesReader = new FileReader(propertiesFile);) {
				int intValueOfChar;
				String targetString = "";

				while ((intValueOfChar = propertiesReader.read()) != -1) {
					targetString += (char) intValueOfChar;
				}
				properties.load(new StringReader(targetString));

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!properties.containsKey("numClients") || !properties.containsKey("port")
					|| !properties.containsKey("db.url") || !properties.containsKey("db.user")
					|| !properties.containsKey("db.password")) {
				System.out.println(
						"Se ha producido un error cargando las propiedades. Revisa que esten todas y vuelve a intentarlo.");
				System.exit(-1);
			}
			// Initialize server
			HybridServer hb = new HybridServer(properties);
			hb.start();

		} else if (args.length == 0) {
			// Initialize server
			HybridServer hb = new HybridServer();
			hb.start();

		} else if (args.length > 1) {

			System.out.println("Introduce solamente 1 argumento y vuelve a intentarlo.");
			System.exit(-1);

		}

	}
}
