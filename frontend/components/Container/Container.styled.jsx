import styled from '@emotion/styled'

import mq from '../../utils/mediaQuery'

export const AdaptiveContainer = styled.div`
	margin: 0px auto;
	padding: 0 16px;
  max-width: 1440px;
	${mq.mobile} {
		min-width: 375px;
		padding: 0 16px;
	}

	${mq.tablet} {
		min-width: 768px;
	}

	${mq.desktop} {
		min-width: 1280px;
		padding: 0 60px;
	}
	${mq.largeDesktop} {
		width: 1560px;
		padding: 0 80px;
	}
`
