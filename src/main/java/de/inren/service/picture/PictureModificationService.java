/**
 * Copyright 2014 the original author or authors.
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
package de.inren.service.picture;

import java.io.File;

import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
public interface PictureModificationService extends Initializable {
    String FILE_NOT_FOUND_DIGEST = "7e8731768b177cb71531b1a99f099b1d";
    String FILE_NOT_FOUND_NAME = "file_not_found.jpg";

    File getImage(String digest);

    File getLayoutImage(String digest);

    File getThumbnailImage(String digest);
}