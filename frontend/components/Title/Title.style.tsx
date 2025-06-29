import { colors, mq } from '@/utils'
import styled from '@emotion/styled'

export const BigTitle = styled.h1<{ isWhite: boolean }>`
	color: ${({ isWhite }) => isWhite && colors.mainWhiteTextColor};

	margin-bottom: 40px;

	@media (min-width: 360px) {
		font-weight: 500;
		font-size: 36px;
		line-height: 42px;
	}
	@media (max-width: 665px) {
	}

	${mq.desktop} {
		font-weight: 500;
		font-size: 45px;
		line-height: 56px;
	}
`
