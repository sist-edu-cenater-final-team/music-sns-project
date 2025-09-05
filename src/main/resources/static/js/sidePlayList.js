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
					resolve(json);

				},
				error: function(xhr, textStatus, errorThrown) {

					
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
	).then((json) => {
		let musicId = [];
		$.each(json, function(index, item) {

			musicId.push(item.musicId);
		})

		if (musicId.length < 1) {
			$('div.recommended-music').html('<p>추천할 음악이 없음.</p>');
			return;
		}

		getMusicList(musicId);

	}).catch((error) => {})
}


function getMusicList(musicId) {
	const authHeader = AuthFunc.getAuthHeader;
	const apiRequest = AuthFunc.apiRequest;

	apiRequest(() =>
		new Promise((resolve, reject) => {
			$.ajax({
				url: "/api/music/musicList",
				data: { musicId: musicId }, // musicId = [1,2,3]
				headers: authHeader(),
				dataType: "json",
				success: function(json) {
					resolve(json);
					
				},
				error: function(xhr, textStatus, errorThrown) {

					
					// axios 스타일의 에러 객체로 변환
					const error = new Error(errorThrown || textStatus);
					error.response = {
						status: xhr.status,
						statusText: xhr.statusText,
						data: xhr.responseJSON || xhr.responseText
					};
					alert("code: " + xhr.status + "\nmessage: " + xhr.responseText + "\nerror: " + error);
					error.request = xhr;
					reject(error);
				}
			});

		})

	).then((json) => {
		const musicList = $('div.recommended-music');

							let v_html = '<ol>';

							json.forEach((item, index) => {  // 배열로 받음
								let artist = '';


								if (item.album.artists.length > 1) {
									for (let i = 0; i < item.album.artists.length; i++) {
										if (i === 0) {
											artist += item.album.artists[i].artistName;
										} else {
											artist += ', ' + item.album.artists[i].artistName;
										}
									}
								} else {
									artist = item.album.artists[0].artistName;
								}

								v_html += `
								  <li class="album-list" onclick="location.href='/music/search?searchType=all&keyword=${item.album.albumName}'">
								      <span class="album-one">
								          <span class="scrolling-wrapper">
								              <span>${index+1}. ${item.track.trackName}</span>
								              <span>${index+1}. ${item.track.trackName}</span>
								          </span>
								      </span>
								      <div class="artist-wrap">
								        <span style="font-size:10pt;" class="music-artist ellipsis-1" title="${artist}">${artist}</span>
								      </div>
								  </li>
								`;


							});

							v_html += '</ol>';
							$('div.recommended-music').html(v_html);
							musicList.html(v_html);
	}).catch((error) => {})

}