import { getPackagingConfig, postPackagingConfig } from '@/services/getListings'
import { AxiosError } from 'axios'
import { FC, useEffect, useState } from 'react'
import { Modal, ModalBody, ModalWrapper } from './CreateModal.styled'

interface ICreateModalProps {
  closeModal: (value: boolean) => void;
  listingId: number ;
}

interface IPackagingConfig {
  id: number;
  pageDesignName: string;
  pageDesignPrice: number;
  pageDesignDescription: string;
  presentationName: string;
  presentationPrice: number;
  presentationDescription: string;
  financialModelName: string;
  financialModelPrice: number;
  financialModelDescription: string;
  discountPercentage: number;
  totalPackagePrice: number;
  createdAt: string;
  updatedAt: string;
}

export const CreateModal: FC<ICreateModalProps> = ({ closeModal , listingId }) => {
  const [packagingConfig, setPackagingConfig] = useState<IPackagingConfig | null>(null);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState<string | null>(null);
  const [projectPackaging , setProjectPackaging] = useState(false)
  const [pageSelected, setPageSelected] = useState(false);
  const [presentationSelected, setPresentationSelected] = useState(false);
  const [financialSelected, setFinancialSelected] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const data = await getPackagingConfig();
        setPackagingConfig(data);
        console.log('Fetched data:', data);
      } catch (error) {
        console.error('Error fetching data:', error);
        const err = error as AxiosError;
        setFetchError(err.message || 'Не удалось загрузить конфигурацию.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleAskPackaging = async () => {
    setProjectPackaging(true)
  }
const handlePackagingConfig = async () => {
  if (!packagingConfig) return;
  try {
    setLoading(true);
    await postPackagingConfig(
      listingId, 
      pageSelected,
      presentationSelected,
      financialSelected
    );
   
  } catch (error) {
    const err = error as AxiosError;
    setFetchError(err.message || 'Не удалось отправить конфигурацию.');
  } finally {
    setLoading(false);
    closeModal(false)
  }
};


return (
  <ModalWrapper>
    <Modal>
      <button className='closeButton' onClick={() => closeModal(false)}>X</button>
      {!projectPackaging &&
       <div>
       <p className='pb-[25px] pt-[25px] tracking-[1.5px]'>
         Ваша заявка на создание объявления была принята.
         Объявление будет опубликовано, как только оно пройдет проверку.
       </p>
       <button className='uploadButton' onClick={handleAskPackaging}>Запросить Упаковку Проекта</button>
     </div>
     }
     
      {projectPackaging && (
        <>
          <h1>Упаковка проекта</h1>

          {loading && <p>Загрузка конфигурации...</p>}
          {fetchError && <p style={{ color: 'red' }}>Ошибка загрузки: {fetchError}</p>}

          {!loading && !fetchError && packagingConfig ? (
            <ModalBody>
              <ul>
                <li>
                  <label>
                    <input
                      type="checkbox"
                      checked={pageSelected}
                      onChange={() => setPageSelected(prev => !prev)}
                    />
                    <h4>{` ${packagingConfig.pageDesignName} - ${packagingConfig.pageDesignPrice}$`}</h4>
                  </label>
                  {pageSelected && <p>{packagingConfig.pageDesignDescription}</p>}
                </li>
                <li>
                  <label>
                    <input
                      type="checkbox"
                      checked={presentationSelected}
                      onChange={() => setPresentationSelected(prev => !prev)}
                    />
                    <h4>
                      {` ${packagingConfig.presentationName} - ${packagingConfig.presentationPrice}$`}
                    </h4>
                  </label>
                  {presentationSelected && <p>{packagingConfig.presentationDescription}</p>}
                </li>
                <li>
                  <label>
                    <input
                      type="checkbox"
                      checked={financialSelected}
                      onChange={() => setFinancialSelected(prev => !prev)}
                    />
                    <h4>
                      {` ${packagingConfig.financialModelName} - ${packagingConfig.financialModelPrice}$`}
                    </h4>
                  </label>
                  {financialSelected && <p>{packagingConfig.financialModelDescription}</p>}
                </li>

                {packagingConfig.discountPercentage > 0 && (
                  <li style={{marginTop: '20px'}}>Процент скидки: {packagingConfig.discountPercentage}%</li>
                )}
                <li style={{marginTop: '10px'}}>Общая цена пакета: {packagingConfig.totalPackagePrice}$</li>
              </ul>

              <button className='uploadButton' onClick={handlePackagingConfig}>Отправить конфигурацию</button>
            </ModalBody>
          ) : (
            !loading && !fetchError && <p>Информация о конфигурации недоступна.</p>
          )}
        </> 
      )}
    </Modal>
  </ModalWrapper>
);
};
