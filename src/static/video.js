$(document).ready(
		function() {
			var add = $('.add')
			$(document).on('drag dragstart dragend dragover dragenter dragleave drop',
					function(e) {
						e.preventDefault();
						e.stopPropagation();
					}
			)
			add.on('dragover dragenter', function(e) {
				$(e.target).addClass('is-dragover');
			}
			).on('dragleave dragend drop', function(e) {
				$(e.target).removeClass('is-dragover');
			}
			).on('drop', function(e) {
				droppedFiles = e.originalEvent.dataTransfer.files;
				console.log(droppedFiles)
			}
			)

			$(".remove").on('click', function() {
				alert('delete')
			})
		})