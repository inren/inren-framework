$('.toggle-view').each(function() {
	$(this).find('.toggle-item').each(function() {
		$(this).click(function() {
			$(this).parent().find('.toggle-detail').each(function() {
				if ($(this).is(':hidden')) {
					$(this).slideDown('200');
				} else {
					$(this).slideUp('200');
				}
			});
			$(this).toggleClass('toggle-visible');
			$(this).toggleClass('toggle-hidden');
		});
	});
});
