package pulleydoreurae.careerquestbackend.community.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.JoinedContest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * @author : parkjihyeok
 * @since : 2024/06/16
 */
@DataJpaTest
@DisplayName("참여했던 공모전 Repository")
@Transactional
@Import(QueryDSLConfig.class)
class JoinedContestRepositoryTest {

	@Autowired UserAccountRepository userAccountRepository;
	@Autowired PostRepository postRepository;
	@Autowired ContestRepository contestRepository;
	@Autowired JoinedContestRepository joinedContestRepository;

	@Test
	@DisplayName("공모전 참여")
	void saveTest() {
	    // Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(user);
		Post post = Post.builder().userAccount(user).title("공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post);
		Contest contest = Contest.builder().post(post).build();
		contestRepository.save(contest);

		// When
		JoinedContest joinedContest = JoinedContest.builder().userAccount(user).contest(contest).build();
		joinedContestRepository.save(joinedContest);
		JoinedContest result = joinedContestRepository.findById(joinedContest.getId()).get();

		// Then
		assertEquals("testId", result.getUserAccount().getUserId());
		assertEquals("공모전입니다.", result.getContest().getPost().getTitle());
	}

	@Test
	@DisplayName("참여했던 공모전 리스트 사용자 정보로 불러오기")
	void findByUserIdTest() {
	    // Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(user);
		Post post1 = Post.builder().userAccount(user).title("1공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post1);
		Post post2 = Post.builder().userAccount(user).title("2공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post2);
		Post post3 = Post.builder().userAccount(user).title("3공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post3);
		Post post4 = Post.builder().userAccount(user).title("4공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post4);
		Contest contest1 = Contest.builder().post(post1).build();
		contestRepository.save(contest1);
		Contest contest2 = Contest.builder().post(post2).build();
		contestRepository.save(contest2);
		Contest contest3 = Contest.builder().post(post3).build();
		contestRepository.save(contest3);
		Contest contest4 = Contest.builder().post(post4).build();
		contestRepository.save(contest4);
		JoinedContest joinedContest1 = JoinedContest.builder().userAccount(user).contest(contest1).build();
		joinedContestRepository.save(joinedContest1);
		JoinedContest joinedContest2 = JoinedContest.builder().userAccount(user).contest(contest2).build();
		joinedContestRepository.save(joinedContest2);
		JoinedContest joinedContest3 = JoinedContest.builder().userAccount(user).contest(contest3).build();
		joinedContestRepository.save(joinedContest3);
		JoinedContest joinedContest4 = JoinedContest.builder().userAccount(user).contest(contest4).build();
		joinedContestRepository.save(joinedContest4);

		// When
		List<JoinedContest> result = joinedContestRepository.findByUserId("testId");

		// Then
		assertEquals(4, result.size());
		assertEquals("4공모전입니다.", result.get(0).getContest().getPost().getTitle());
	}

	@Test
	@DisplayName("공모전ID + UserId로 제거")
	void deleteByContestIdAndUserIdTest() {
	    // Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(user);
		Post post = Post.builder().userAccount(user).title("공모전입니다.").content("공모전내용").postCategory(PostCategory.CONTEST).view(0L).build();
		postRepository.save(post);
		Contest contest = Contest.builder().post(post).build();
		contestRepository.save(contest);
		JoinedContest joinedContest = JoinedContest.builder().userAccount(user).contest(contest).build();
		joinedContestRepository.save(joinedContest);

	    // When
		joinedContestRepository.deleteByContestIdAndUserAccount(contest.getId(), user);
		Optional<JoinedContest> result = joinedContestRepository.findById(joinedContest.getId());

		// Then
		assertEquals(Optional.empty(), result);
	}
}
