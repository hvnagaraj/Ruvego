package com.ruvego.project.server;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("upload")
public class FileUploadServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String UPLOAD_DIRECTORY;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String serverPath = getServletContext().getRealPath("/"); 
		UPLOAD_DIRECTORY = serverPath + "icons/";

		// process only multipart requests
		if (ServletFileUpload.isMultipartContent(req)) {

			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				System.out.println("Server: File Uploading");
				@SuppressWarnings("unchecked")
				List<FileItem> items = upload.parseRequest(req);
				System.out.println(items.toString());
				for (FileItem item : items) {
					// process only file upload - discard other form item types
					if (item.isFormField()) continue;

					String fileName = item.getName();
					// get only the file name not whole path
					if (fileName != null) {
						fileName = FilenameUtils.getName(fileName);
					}

					File uploadedFile;
					while (true) {
						uploadedFile = new File(UPLOAD_DIRECTORY, fileName);
						System.out.println("Server: File being created at : " + UPLOAD_DIRECTORY + fileName);
						if (uploadedFile.createNewFile()) {
							item.write(uploadedFile);
							resp.setStatus(HttpServletResponse.SC_CREATED);
							resp.getWriter().print("Created filename : " + fileName);
							resp.flushBuffer();
							System.out.println("Server: Image successfully added");
							break;
						} else {
							Random rand = new Random();
							int pick = 0;
							for (int j = 0; j < 10; j++) {
								pick = rand.nextInt(10);
							}

							String delims;
							String[] tokens = null;
							System.out.println("Filename already exists : " + fileName);

							if (fileName.contains("jpg")) {
								delims = ".jpg";
								tokens = fileName.split(delims);

								fileName = tokens[0] + "_" + pick + ".jpg";

								System.out.println("File: " + tokens[0]);
							} else if (fileName.contains("png")) {
								delims = ".png";
								tokens = fileName.split(delims);

								fileName = tokens[0] + "_" + pick + ".png";

								System.out.println("File: " + tokens[0]);

							} else {
								throw new IOException("The file already exists in repository.");
							}


							System.out.println("Error: adding the new file");

						}
					}
				}
			} catch (Exception e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while creating the file : " + e.getMessage());
			}

		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
	}
}