var uploadCount = 0;

$(document).ready(
		function() {
			var add = $('.drop')
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
				uploadCount += droppedFiles.length;
				for (var i=0, l=droppedFiles.length; i<l; i++) {
					uploadFile(droppedFiles[i], $(e.target))
				}
			}
			)

			$(".remove").on('click', function(e) {
				remove($(e.target))
			})
		}
)

function remove(target) {
	var xhr = new XMLHttpRequest()
	var id = target.parent().attr('id')
	xhr.open("post", "/remove/"+id, true)
	xhr.addEventListener('loadend', function(){
		location.reload();
	})
	xhr.send();
}
		
function uploadFile(file, target) {
	var xhr = new XMLHttpRequest()
	
	var id = target.attr('id')
	var slot = $(target)
	slot = slot.parent()
	var x =	$("<div class='slot'><div class='title head'>Upload</div><div class='progress'></div></div>")
	x.attr('id', file.name)
	slot.append(x)

	var progressBar = x.children(".progress")
	
	xhr.upload.addEventListener("progress", function (evt) {
		if (evt.lengthComputable) {
			progressBar.text((evt.loaded / evt.total) * 100 + "%")
//			progressBar.style.width = (evt.loaded / evt.total) * 100 + "%";
		}
	})
	
	xhr.upload.addEventListener("load", function(evt) {
		uploadCount--;
		if (uploadCount == 0) {
			location.reload();
		}
	})
	
	xhr.open("post", "/upload/"+id, true)
	xhr.setRequestHeader("X-File-Name", file.name)
	xhr.setRequestHeader("X-File-Size", file.size)
	xhr.setRequestHeader("X-File-Type", file.type)
	xhr.setRequestHeader("X-File-Timestamp", file.lastModified)
	
	xhr.send(file)
}