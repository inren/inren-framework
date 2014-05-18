/*
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
	if (!$.jqplot.use_excanvas) {
        $('div.jqplot-target').each(function(){
            var outerDiv = $(document.createElement('div'));
            var header = $(document.createElement('div'));
            var div = $(document.createElement('div'));

            outerDiv.append(header);
            outerDiv.append(div);

            outerDiv.addClass('jqplot-image-container');
            header.addClass('jqplot-image-container-header');
            div.addClass('jqplot-image-container-content');

            header.html('Right Click to Save Image As...');

            var close = $(document.createElement('a'));
            close.addClass('jqplot-image-container-close');
            close.html('Close');
            close.attr('href', '#');
            close.click(function() {
                $(this).parents('div.jqplot-image-container').hide(500);
            });
            header.append(close);

            $(this).after(outerDiv);
            outerDiv.hide();

            outerDiv = header = div = close = null;

            if (!$.jqplot._noToImageButton) {
                var btn = $(document.createElement('button'));
                btn.text('View Plot Image');
                btn.addClass('jqplot-image-button');
                btn.bind('click', {chart: $(this)}, function(evt) {
                    var imgelem = evt.data.chart.jqplotToImageElem();
                    var div = $(this).nextAll('div.jqplot-image-container').first();
                    div.children('div.jqplot-image-container-content').empty();
                    div.children('div.jqplot-image-container-content').append(imgelem);
                    div.show(500);
                    div = null;
                });

                $(this).after(btn);
                btn.after('<br />');
                btn = null;
            };
        });
    };
