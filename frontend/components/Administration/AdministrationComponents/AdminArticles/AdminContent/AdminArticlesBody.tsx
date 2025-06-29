import { AdminBlog } from './AdministrationComponents/AdminBlog'
import { AdminNews } from './AdministrationComponents/AdminNews'
export const AdminArticlesBody = ({ activeTab }: { activeTab: number }) => {
	return (
		<div className='mt-16'>
			{activeTab === 0 && <AdminBlog />}
			{activeTab === 1 && <AdminNews />}
		</div>
	)
}
