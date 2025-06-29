import { mq } from '@/utils'
import styled from '@emotion/styled'

export const CatalogSideBar = styled.div`
	display:flex;
	flex-direction: column;
	
	${mq.tablet} {
	}

	${mq.desktop} {
	}
	${mq.largeDesktop} {
		row-gap:10px;
	}
`
