import { colors } from '@/utils'
import styled from '@emotion/styled'


export const Aside = styled.div<{ isChatOpen: boolean }>`
  padding: 20px;
  border: 1px solid ${colors.borderColor};
  border-radius: 20px;
  max-height: 100%;
  overflow-y: auto;
  flex: 1;

  @media (max-width: 880px) {
    display: ${(props) => (props.isChatOpen ? 'none' : 'flex')};
    flex-direction: column;
  }
`;
export const AsideItems = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 4px;
`
export const AsideSearch = styled.input`
	border-radius: 180px;
`
