package pulleydoreurae.careerquestbackend.common.converter;

import java.util.StringTokenizer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pulleydoreurae.careerquestbackend.team.domain.TeamType;

/**
 * URL로 들어온 문자열을 TeamType과 매핑하는 컨버터
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Component
public class StringToTeamTypeConverter implements Converter<String, TeamType> {

	@Override
	public TeamType convert(String source) {
		StringTokenizer st = new StringTokenizer(source, "-");
		StringBuilder sb = new StringBuilder();

		while (st.hasMoreTokens()) {
			sb.append(st.nextToken().toUpperCase());
			if (st.hasMoreTokens()) {
				sb.append("_");
			}
		}
		return TeamType.valueOf(sb.toString());
	}
}
