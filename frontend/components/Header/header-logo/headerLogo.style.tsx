import { mq } from '@/utils'
import styled from '@emotion/styled'

export const Logo = styled.h2`
	width: 208px;
	margin-right: 10px;
	cursor: pointer;
	${mq.smallMobile} {
		width: 132px;
		
	}
	${mq.tablet} {
		width: 132px;
		
	}

	${mq.desktop} {
		width: 162px;

	}
	${mq.largeDesktop} {
	}
`
