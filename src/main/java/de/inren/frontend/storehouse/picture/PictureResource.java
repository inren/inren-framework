/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.storehouse.picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.service.picture.PictureModificationService;

/**
 * The static Url methods are creating urls like this:
 * http://[servername]/[appname
 * ]/resources/org.apache.wicket.Application//pictureResource?id=[digest]
 * http://[servername]/[appname]/resources/org.apache.wicket.Application//
 * pictureResource?id=[digest]&layout
 * http://[servername]/[appname]/resources/org
 * .apache.wicket.Application//pictureResource?id=[digest]&thumb
 * 
 * Diese Urls are bookmarkeable.
 * 
 * @author Ingo Renner
 * @author Henning Teek
 * 
 */
public class PictureResource extends DynamicImageResource {
	private final Logger log = LoggerFactory.getLogger(PictureResource.class);

	private static final String LAYOUT = "layout";

	private static final String THUMB = "thumb";

	public static final String ID = "id";

	public static final String SIZE = "size";

	@SpringBean
	private PictureModificationService pictureModificationService;

	protected static final String FORMAT = "jpg";

	public static final String PICTURE_RESOURCE = "pictureResource";

	public PictureResource() {
		super();

		log.info("injecting 1");
		Injector.get().inject(this);
	}

	public static ResourceReference asReference() {
		return new ResourceReference("pictureResource") {

			@Override
			public IResource getResource() {
				return new PictureResource();
			}
		};
	}

	public static String getUrl(String digest) {

		String baseUrl = RequestCycle.get().getRequest().getContextPath();
		String url = baseUrl.toString();

		return url;

		// final ResourceReference imageResource = new UrlResourceReference(
		// PICTURE_RESOURCE);
		// return RequestCycle.get().urlFor(imageResource.getResource()) + "?"
		// + ID + "=" + digest;
	}

	// public static String getLayoutUrl(String digest) {
	// ResourceReference imageResource = new
	// ResourceReference(PICTURE_RESOURCE);
	// return RequestCycle.get().urlFor(imageResource) + "?" + ID + "=" + digest
	// + "&" + LAYOUT;
	// }
	//
	// public static String getThumbnailUrl(String digest) {
	// ResourceReference imageResource = new
	// ResourceReference(PICTURE_RESOURCE);
	// return RequestCycle.get().urlFor(imageResource) + "?" + ID + "=" + digest
	// + "&" + THUMB;
	// }

	// protected Time getImageLastModified() {
	// ValueMap params = getParameters();
	// File file = getImageFile(params);
	// if (file != null) {
	// return Time.valueOf(file.lastModified());
	// }
	// return null;
	// }

	// @Override
	// protected synchronized ResourceState getResourceState() {
	// return new ResourceState() {
	// private byte[] imageData;
	//
	// @Override
	// public Time lastModifiedTime() {
	// if (lastModifiedTime == null) {
	// lastModifiedTime = getImageLastModified();
	// if (lastModifiedTime == null) {
	// lastModifiedTime = Time.now();
	// }
	// }
	// return lastModifiedTime;
	// }
	//
	// @Override
	// public byte[] getData() {
	// if (imageData == null) {
	// imageData = getImageData();
	// }
	// return imageData;
	// }
	//
	// @Override
	// public String getContentType() {
	// return "image/" + FORMAT;
	// }
	// };
	// }

	@Override
	protected byte[] getImageData(Attributes attributes) {
		PageParameters params = attributes.getParameters();
		File file = getImageFile(params);
		if (file != null) {
			try {
				return IOUtils.toByteArray(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				log.error("file not found", e);
			} catch (IOException e) {
				log.error("io error", e);
			}
		}
		log.error("No image data available.");
		return new byte[0];
	}

	private File getImageFile(PageParameters params) {
		log.info("injecting getImageFile");

		// // TODO nötig????????????
		// Injector.get().inject(this);

		if (!params.get(ID).isEmpty()) {
			final String digest = params.get(ID).toString();
			log.info("digest=" + digest);
			log.info("url is :  " + getUrl(digest));

			log.info("pictureModificationService=" + pictureModificationService);
			if (!params.get(SIZE).isEmpty()) {
				final String size = params.get(SIZE).toString();
				if (LAYOUT.equals(size)) {
					return pictureModificationService.getLayoutImage(digest);
				}
				if (THUMB.equals(size)) {
					return pictureModificationService.getThumbnailImage(digest);
				}
			}
			return pictureModificationService.getImage(digest);
		}
		return null;
	}

}
