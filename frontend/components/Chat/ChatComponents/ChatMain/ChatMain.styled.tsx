import { colors } from '@/utils'
import styled from '@emotion/styled'

interface IChatMainProps {
	background?: string
	color?: string
}

export const ChatMainContainer = styled.div<{ isChatOpen: boolean }>`
	display: flex;
	flex-direction: column;
	width: 100%;
	height: 100%;
	border-radius: 24px;
	padding: 20px;
	border: 1px solid ${colors.borderColor};
	flex: 2;

	@media (max-width: 880px) {
		display: ${props => (props.isChatOpen ? 'flex' : 'none')};
		flex-direction: column;
	}
`

export const ChatMainHeader = styled.div`
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-bottom: 20px;
	border-bottom: 1px solid ${colors.borderColor};
`

export const ChatMainBody = styled.div`
	display: flex;
	flex-direction: column;
	gap: 20px;
	height: 550px;
	max-width: 100%;
	padding: 25px 25px 25px 0;
	overflow-y: auto;
	flex-grow: 1;
`
export const Message = styled.div<IChatMainProps>`
	display: flex;
	flex-direction: column;
	position: relative;
	padding: 16px;
	border-radius: 20px;
	width: fit-content;
	max-width: 65%;
	word-break: break-word;
	color: ${props => props.color || colors.mainTextColor};
	background: ${props => props.background || colors.borderColor};
`

export const ChatInput = styled.input`
	position: relative;
	border: 1px solid ${colors.borderColor};
	border-radius: 180px;
	padding: 15px 100px 15px 20px;
	min-width: 100%;
	outline: none;
	&:placeholder {
		color: ${colors.ThirdGreyTextColor};
	}
`
export const EmptyMessage = styled.span`
	display: flex;
	align-items: center;
	justify-content: center;
	width: 100%;
	height: 100%;
	font-size: 24px;
	line-height: 36px;
`
