import { SideBarItem } from './SideBarItem/SideBarItem'
interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
	isAdmin?: boolean
}
export const Sidebar = ({setActiveTab, activeTab , isAdmin}: Props) => {
	return(
		<>
			<SideBarItem isAdmin={isAdmin} activeTab = {activeTab} setActiveTab={setActiveTab}/>
		</>
	)
}