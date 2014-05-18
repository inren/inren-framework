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

import java.util.List;

import de.inren.data.domain.picture.Picture;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
public interface PictureService extends Initializable {
    
    Picture savePicture(Picture picture);

    void deletePicture(Picture picture);

    Picture loadPicture(Long id);

    List<Picture> loadAllPicture();
}
