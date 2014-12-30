/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.inren.frontend.storehouse.picture;

/**
 * Pictures are stored with there original raw data, but they can be accessed in
 * differed scaled sizes. Scaling is done by the PictureModificationService on
 * demand.
 * 
 * @author Ingo Renner
 * 
 */
public enum PictureVariantType {
    THUMBNAIL(1), PREVIEW(2), LAYOUT(3);
    private int type;

    private PictureVariantType(int type) {
        this.type = type;
    }

    public static PictureVariantType forType(int type) {
        switch (type) {
            case 1:
                return THUMBNAIL;
            case 2:
                return PREVIEW;
            case 3:
                return LAYOUT;
            default:
                throw new RuntimeException("Unknown variant type: [" + type + "]");
        }
    }

    public int getType() {
        return type;
    }
}
