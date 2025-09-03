$(function() {
	
	const authHeader = AuthFunc.getAuthHeader;
	const apiRequest = AuthFunc.apiRequest;

	apiRequest(() =>
		new Promise((resolve, reject) => {
			$.ajax({
				url: '/api/userInfo/getInfo',
				headers: authHeader(),
				dataType: "json",
				success: function(json) {
					const profile = $('div#profileLayer');

					let v_html = `
	                <div class="inner" style="background-color: #F2F0FF;">
	                    <div class="text-center" style="font-weight:bold; font-size: 16pt;">${json.myuser.username}</div>
	                    <div class="text-center" style="font-size: 12pt; margin-top: 10px;">${json.myuser.nickname}</div>
	                    <div>
	                        <img src="${json.myuser.profile_image}" class="rounded-circle mr-4 profile-img"
	                             style="width: 128px; height: 128px; margin: 10px 85px; cursor:pointer;"
	                             onclick="location.href='/mypage/myinfo'" />
	                    </div>
	                    <div class="flex stats">
	                        <div>
	                            <div class="font-bold number-color">${json.myuser.postCount}</div>
	                            <div class="label-text">게시물</div>
	                        </div>
	                        <div>
	                            <div class="font-bold number-color" style="cursor:pointer;"
	                                 onclick="location.href='/mypage/myFollowers'">${json.myuser.followeeCount}</div>
	                            <div class="label-text">팔로워</div>
	                        </div>
	                        <div>
	                            <div class="font-bold number-color" style="cursor:pointer;"
	                                 onclick="location.href='/mypage/myFollowers'">${json.myuser.followerCount}</div>
	                            <div class="label-text">팔로우</div>
	                        </div>
	                    </div>
	                </div>

	                <div class="inner">
	                    <div class="font-bold" style="display:flex; justify-content: space-between;">
	                        <span>${json.myuser.nickname}님의 프로필 음악</span> 
	                        <button style="margin-right: 3px;" id="myPlayList">+</button>
	                    </div>
	                    <div class="playlist-section">
	                        <ul class="playlist-list mt-3">
	                `;

					// profileMusic 가져오기
					apiRequest(() =>
						new Promise((resolve2, reject2) => {
							$.ajax({
								url: "/api/music/myProfileMusic",
								headers: authHeader(),
								dataType: "json",
								success: function(profileMusicJson) {
									// musicId 배열 생성
									let musicIdArr = profileMusicJson.map(item => item.musicId);

									if (musicIdArr.length === 0) {
										v_html += `<li>등록된 음악이 없습니다.</li></ul></div></div>`;
										profile.html(v_html);
										return;
									}

									// 한 번에 musicList 가져오기
									$.ajax({
										url: "/api/music/musicList",
										data: { musicId: musicIdArr },
										headers: authHeader(),
										dataType: "json",
										success: function(musicJson) {
											musicJson.forEach((item, index) => {
												let artist = '';
												if (item.album.artists.length > 1) {
													artist = item.album.artists.map(a => a.artistName).join(', ');
												} else {
													artist = item.album.artists[0].artistName;
												}

												v_html += `
	                                            <li class="playlist-item">
	                                                <img src="${item.album.albumImageUrl}" style="width: 40px; height: 40px; margin-right: 10px;"/>
	                                                <div class="song-title">
	                                                    <strong>${item.album.albumName}</strong>
	                                                    <div class="song-artist">
	                                                        <div class="scrolling-wrapper scrolling">
	                                                            <span>${artist}</span>
	                                                            <span>${artist}</span>
	                                                        </div>
	                                                    </div>
	                                                </div>
	                                                <div class="song-button">
	                                                    <button>x</button>
	                                                </div>
	                                            </li>
	                                            `;
											});

											v_html += `</ul></div></div>`;
											profile.html(v_html);
										},
										error: function() {
											alert("음악 정보를 불러오는데 실패했습니다.");
										}
									});
								},
								error: function() {
									alert("프로필 음악을 불러오는데 실패했습니다.");
								}
							});
						})
					);
				},
				error: function(xhr, textStatus, errorThrown) {
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
	);

	$(document).on('click', 'button#myPlayList', function() {
		location.href = "/mypage/playList";
	});
}); // end of $(function(){})

