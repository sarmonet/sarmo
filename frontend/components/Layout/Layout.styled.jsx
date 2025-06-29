import { mq } from '@/utils'
import styled from '@emotion/styled'

export const Wrapper = styled.div`
	display: grid;
	grid-template-rows: auto 1fr auto; 

	${mq.tablet} {
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
	}
`

export const Main = styled.main`
	width: 100%;
	min-height: 100vh;
	margin-top: 111px;
	${mq.tablet} {
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
	}
`
