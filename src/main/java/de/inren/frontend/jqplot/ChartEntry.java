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
package de.inren.frontend.jqplot;

import java.io.Serializable;

/**
 * @author Ingo Renner
 *
 */
public class ChartEntry implements Serializable {

    private final String x;
    
    private final String y;
    
    public ChartEntry(String x, String y) {
        this.x = x;
        this.y = y;
    }
    
    public String getX() {
        return x;
    }
    
    public String getY() {
        return y;
    }

}
