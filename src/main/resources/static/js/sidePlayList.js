$(function() {
	musicList(1);


	$("button#emotion").on("click", function() {
		$(".emotions .btn").removeClass("active"); // 기존 active 제거
		$(this).addClass("active"); // 현재 클릭한 버튼만 active
		let emotionId = $(this).val();
		musicList(emotionId);
	});



}); // end of function(){}

function musicList(emotionId) {
	const authHeader = AuthFunc.getAuthHeader;
	const apiRequest = AuthFunc.apiRequest;

	apiRequest(() =>
		new Promise((resolve, reject) => {
			$.ajax({
				url: '/api/music/palyList',
				data: { "emotionId": emotionId },
				headers: authHeader(),
				dataType: "json",
				success: function(json) {

					const musicList = $('div.recommended-music');

					let v_html = `<ol>`;
					let musicId = [];
					$.each(json, function(index, item) {
						
						musicId.push(item.musicId);
						})

						$.ajax({
						    url: "/api/music/musicList",
						    data: { musicId: musicId }, // musicId = [1,2,3]
						    headers: authHeader(),
						    dataType: "json",
						    success: function(json) {
						        console.log(json);

						        let v_html = '<ol>';

						        json.forEach((track, index) => {  // 배열로 받음
						            let artist = '';
						            if (track.album.artists.length > 1) {
						                for (let i = 0; i < track.album.artists.length; i++) {
						                    if (i === 0) {
						                        artist += track.album.artists[i].artistName;
						                    } else {
						                        artist += ', ' + track.album.artists[i].artistName;
						                    }
						                }
						            } else {
						                artist = track.album.artists[0].artistName;
						            }

						            v_html += `
						                <li class="music-item" onclick="location.href='/music/search?searchType=all&keyword=${track.album.albumName}'">
						                    <span class="music-title">${index + 1}. ${track.album.albumName}</span>
						                    <div class="artist-wrap">
						                        <span class="music-artist">${artist}</span>
						                    </div>
						                </li>
						            `;
						        });

						        v_html += '</ol>';
						        $('div.recommended-music').html(v_html);
						    },
						    error: function() {
						        alert("노래 불러오기 실패!");
						    }
						});


					v_html += `</ol>`;
					musicList.html(v_html);
				},
				error: function(xhr, textStatus, errorThrown) {

					alert("code: " + xhr.status + "\nmessage: " + xhr.responseText + "\nerror: " + error);
					// axios 스타일의 에러 객체로 변환
					const error = new Error(errorThrown || textStatus);
					error.response = {
						status: xhr.status,
						statusText: xhr.statusText,
						data: xhr.responseJSON || xhr.responseText
					};
					error.request = xhr;
					reject(error);
				}

			});
		})
	)
}