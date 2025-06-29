import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { postUserDoc, putUserImg } from '@/services/getUsers'
import { postDoc, postImage } from '@/services/uploadFiles'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
import { useState } from 'react'
import toast from 'react-hot-toast'
import { ProfileTitle } from '../ProfileContent.styled'
import {
  ProfileDoc,
  ProfileDocBtn,
  ProfileImagesBlock,
  ProfileUpload
} from './ProfileBasic.styled'
export const ProfileBasic = () => {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState('');
	const {user} = useCatalog()
  const { t } = useTranslation('common');
  const imageUrl = user?.profilePictureUrl || '/images/user/altUser.png';
 const handleUploadDoc = async () => {
  try {
    if (!selectedFile) return;

    const documentUrl = await postDoc(selectedFile);

    await postUserDoc(documentUrl);

  } catch (error) {
    console.error('❌ Ошибка при загрузке документа:', error);
    setError('Произошла ошибка при загрузке документа.');
  }
};


const handleImageUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
  if (event.target.files?.[0]) {
    const file = event.target.files[0];
    setSelectedFile(file);
    toast.success('Изображение успешно загружено! Обновите страницу, чтобы увидеть изменения.');  
    try {
      const imageUrl = await postImage(file); 
      await putUserImg(imageUrl);             
    } catch (err) {
      console.error('❌ Ошибка при загрузке изображения:', err);
      setError('Ошибка при загрузке изображения.');
    }
  }
};



  return (
    <>
      <ProfileTitle bgc="#B5E4CA">{user?.firstName || 'User'} {user?.lastName || 'Name'}</ProfileTitle>
      <ProfileImagesBlock>
        <Image src={imageUrl || '/images/user/altUser.png'} alt="Ava" width={96} height={96} />
        
        <label style={{ cursor: 'pointer' , display: 'flex' , justifyContent:'center' }}>
          <ProfileUpload as="span" >{t('buttons.uploadNewProfileImage')}</ProfileUpload>
          <input type="file" accept="image/*" onChange={handleImageUpload} style={{ display: 'none' }} />
        </label>

        {/* <ProfileRemove>Remove</ProfileRemove> */}
      </ProfileImagesBlock>
			<ProfileTitle style={{marginTop: '10px'}} bgc= {colors.btnMainColor}>{t('profilePage.yourDoc')}</ProfileTitle>
      <ProfileDoc style={{ marginTop: '20px' }}>
        <input
          type="file"
          accept=".pdf,.doc,.docx,.jpg,.png"
          onChange={(e) => {
            if (e.target.files && e.target.files[0]) {
              setSelectedFile(e.target.files[0]);
            }
          }}
        />
        <ProfileDocBtn
          onClick={handleUploadDoc}
          
        >
          {t('buttons.uploadNewProfileDoc')}
        </ProfileDocBtn>
      </ProfileDoc>

      {error && <p style={{ color: 'red' }}>{error}</p>}
    </>
  );
};
