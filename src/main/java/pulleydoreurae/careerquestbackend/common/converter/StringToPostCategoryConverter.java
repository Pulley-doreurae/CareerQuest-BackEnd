package pulleydoreurae.careerquestbackend.common.converter;

import java.util.StringTokenizer;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pulleydoreurae.careerquestbackend.community.domain.PostCategory;

/**
 * URL로 들어온 문자열을 PostCategory와 매핑하는 컨버터
 *
 * @author : parkjihyeok
 * @since : 2024/05/24
 */
@Component
public class StringToPostCategoryConverter implements Converter<String, PostCategory> {

	@Override
	public PostCategory convert(String source) {
		StringTokenizer st = new StringTokenizer(source, "-");
		StringBuilder sb = new StringBuilder();

		while (st.hasMoreTokens()) {
			sb.append(st.nextToken().toUpperCase());
			if (st.hasMoreTokens()) {
				sb.append("_");
			}
		}
		return PostCategory.valueOf(sb.toString());
	}
}
