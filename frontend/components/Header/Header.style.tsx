import { mq } from '@/utils'
import styled from '@emotion/styled'

export const HeaderWrapper = styled.div`
	position: fixed;
	top: 0;
	left: 0;
	background: white;
	width: 100%;
	z-index: 999;
	border-bottom: 1px solid #e5e5e5;
`
export const HeaderBody = styled.div`
	display: flex;
	align-items: center;
	width: fit-content;
	justify-content: space-between;
	margin: 0 auto;
	height: 111px;

	${mq.tablet} {
		column-gap: 10px;
		padding: 23px 0;
	}

	${mq.desktop} {
		padding: 0;
	}
	${mq.largeDesktop} {
		padding: 0;
	}
`
