import { ICatalogSub } from '@/components/Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { IField } from '@/components/Catalog/Catalog-data/CatalogInterface/Filter.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { ProfileTitle } from '@/components/Profile/ProfileContent/ProfileContent.styled'
import { getSubCategories } from "@/services/getCategories"
import { getFieldsById, getInvestFieldsById, getListingById, ICreateListing, updateListing } from '@/services/getListings'
import { postDoc, postImage, postImages, postVideo } from "@/services/uploadFiles"
import { colors } from '@/utils'
import { Checkbox, FormControlLabel, FormGroup } from '@mui/material'
import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { ListingButton, ListingField, ListingLabel } from '../Listings.styled'
interface ListingUpdateProps {
  listingId: number;
  onClose: () => void;
}

export const EditingListing = ({ listingId , onClose }: ListingUpdateProps) => {
  const {
    categories,
    setSubCategories,
    setActiveCategory,
    setFields,
    fields,
  } = useCatalog();

  const [formData, setFormData] = useState<ICreateListing | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
  const [selectedVideo, setSelectedVideo] = useState<File | null>(null);
  const [selectedDynamicFiles, setSelectedDynamicFiles] = useState<Record<string, File | null>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [, setSelectedSubCategory] = useState<ICatalogSub | null>(null);

  const listFieldOptions: Record<string, string[]> = {
    "Этап развития бизнеса": [
      "Идея",
      "MVP",
      "Стадия роста",
      "Прибыльный бизнес",
    ],
    "Форма участия": [
      "Доля в компании",
      "Займ",
      "Менторство + деньги",
    ],
    "Тип сделки": [
      "Венчурные инвестиции",
      "Выкуп доли (частичной / полной)",
      "Совместное предприятие",
      "Кредит",
    ]
  };
  useEffect(() => {
    const fetchListing = async () => {
      try {
        const listing = await getListingById({id : listingId});
        setFormData(listing);
        const category = categories?.find(cat => cat.id === listing.categoryId);
        if (category) setActiveCategory(category);

        const subCategoriesData = await getSubCategories({ id: listing.category.id });
        setSubCategories(subCategoriesData);

        const matchedSub = subCategoriesData.find(sub => sub.id === listing.subCategoryId);
        if (matchedSub) setSelectedSubCategory(matchedSub);
       
        if(listing.invest === true){
          const fieldsData = await getInvestFieldsById({  id: listing.category.id });
          setFields(fieldsData);
        }else if(listing.invest === false){
          const fieldsData = await getFieldsById({  id: listing.category.id });
          setFields(fieldsData);
        }
  
        
       

      } catch (error) {
        console.error("❌ Ошибка при загрузке объявления:", error);
      }
    };

    fetchListing();
  }, [listingId, categories]);

 const handleFieldChange = (fieldName: string, value: string) => {
  if (!formData) return;

  const fieldType = fields?.fields?.find(f => f.name === fieldName)?.type;

  if (fieldType === "Double") {
    const parsed = parseFloat(value);
    if (isNaN(parsed) && value !== "") {
      toast.error(`Поле "${fieldName}" должно быть числом с плавающей точкой`);
      return;
    }
  }

  setFormData(prev => ({
    ...prev!,
    fields: {
      ...prev!.fields,
      [fieldName]: value,
    }
  }));
};


  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.[0]) setSelectedFile(e.target.files[0]);
  };

  const handleMultipleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (files) {
      setSelectedFiles(Array.from(files));
    }
  };

  const handleVideoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files?.[0]) setSelectedVideo(e.target.files[0]);
  };

  const handleDynamicFileChange = (field: string, e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setSelectedDynamicFiles(prev => ({
      ...prev,
      [field]: file
    }));
  };
  const handleMultiSelectChange = (fieldName: string, option: string, checked: boolean) => {
    if(!formData) return;
    const currentRaw = formData.fields[fieldName];
    const currentValues = Array.isArray(currentRaw) ? currentRaw : [];
  
    const updatedValues = checked
      ? [...new Set([...currentValues, option])]
      : currentValues.filter((val: string) => val !== option);
  
     // eslint-disable-next-line @typescript-eslint/no-explicit-any
      setFormData((prev: any) => ({
        ...prev,
        fields: {
          ...prev.fields,
          [fieldName]: updatedValues, 
        },
      }));
      
  };
  const handleUpdate = async () => {
  if (!formData) return;

  setIsSubmitting(true);

  // === 1. Проверка обязательных полей ===

  if (!formData.title || !formData.description || !formData.price ||
      !formData.country || !formData.city || !formData.fullAddress) {
    toast.error("Пожалуйста, заполните все обязательные поля основной информации.");
    setIsSubmitting(false);
    return;
  }

  if (isNaN(Number(formData.price))) {
    toast.error("Цена должна быть числом.");
    setIsSubmitting(false);
    return;
  }

  // === 2. Проверка обязательных кастомных полей ===

  if (fields?.fields) {
    for (const field of fields.fields) {
      const value = formData.fields[field.name];
      const file = selectedDynamicFiles[field.name];

      if (field.required) {
        if (field.type === 'Boolean') {
          if (value === undefined) {
            toast.error(`Пожалуйста, заполните обязательное булевое поле: "${field.name}"`);
            setIsSubmitting(false);
            return;
          }
        } else if (field.type === 'File') {
          if (!value && !file) {
            toast.error(`Пожалуйста, загрузите обязательный файл: "${field.name}"`);
            setIsSubmitting(false);
            return;
          }
        } else {
          if (!value || (typeof value === 'string' && value.trim() === '')) {
            toast.error(`Пожалуйста, заполните обязательное поле: "${field.name}"`);
            setIsSubmitting(false);
            return;
          }
        }
      }

      if ((field.type === 'Double' || field.type === 'Integer') && value && isNaN(Number(value))) {
        toast.error(`Поле "${field.name}" должно быть числом.`);
        setIsSubmitting(false);
        return;
      }
    }
  }

  // === 3. Продолжение загрузки и отправки данных ===

  try {
    const uploadPromises: Promise<{ fieldName: string; url: string; }>[] = [];

    if (selectedFile) {
      uploadPromises.push(postImage(selectedFile).then(url => ({ fieldName: 'mainImage', url })));
    }

    if (selectedVideo) {
      uploadPromises.push(postVideo(selectedVideo).then(url => ({ fieldName: 'videoUrl', url })));
    }

    if (fields?.fields) {
      for (const field of fields.fields) {
        if (field.type === 'File' && selectedDynamicFiles[field.name]) {
          uploadPromises.push(
            postDoc(selectedDynamicFiles[field.name]!).then(url => ({
              fieldName: field.name,
              url
            }))
          );
        }
      }
    }

    const uploadResults = await Promise.all(uploadPromises);
    const uploadedUrls = Object.fromEntries(uploadResults.map(({ fieldName, url }) => [fieldName, url]));
    const galleryUrls: string[] = [];

    if (selectedFiles.length > 0) {
      const savedUrls = await postImages(selectedFiles);
      galleryUrls.push(...savedUrls);
    }

    const processedFields: Record<string, string | number | boolean | string[]> = {};
    fields?.fields.forEach(field => {
      const fieldName = field.name;
      const fieldType = field.type;
      const rawValue = uploadedUrls[fieldName] ?? formData.fields[fieldName];

      let finalValue: string | number | boolean | undefined;

      if (fieldType === "File") {
        finalValue = uploadedUrls[fieldName] || '';
        if (finalValue) {
          processedFields[fieldName] = finalValue;
        }

      } else if (fieldType === "Boolean") {
        if (rawValue !== undefined) {
          processedFields[fieldName] = String(rawValue) === 'true';
        } else if (field.required) {
          processedFields[fieldName] = false;
        }

      } else if (fieldType === "Integer") {
        const intVal = parseInt(String(rawValue), 10);
        if (!isNaN(intVal)) {
          processedFields[fieldName] = intVal;
        } else if (field.required) {
          processedFields[fieldName] = 0;
        }

      } else if (fieldType === "Double") {
        const floatVal = parseFloat(String(rawValue));
        if (!isNaN(floatVal)) {
          processedFields[fieldName] = Number.isInteger(floatVal) ? floatVal + 0.00001 : floatVal;
        } else if (field.required) {
          processedFields[fieldName] = 0.00001;
        }

      } else if (fieldType === "List") {
        if (Array.isArray(rawValue)) {
          processedFields[fieldName] = rawValue;
        } else if (typeof rawValue === "string" && rawValue !== "") {
          processedFields[fieldName] = [rawValue];
        } else if (field.required) {
          processedFields[fieldName] = [];
        }

      } else {
        if (rawValue !== undefined && rawValue !== null) {
          processedFields[fieldName] = String(rawValue);
        } else if (field.required) {
          processedFields[fieldName] = "";
        }
      }
    });

    const updatedData: ICreateListing = {
      ...formData,
      mainImage: uploadedUrls['mainImage'] || formData.mainImage,
      images: galleryUrls,
      videoUrl: uploadedUrls['videoUrl'] || formData.videoUrl,
      price: Number(formData.price),
      fields: processedFields,
    };

    await updateListing(listingId, updatedData);
    toast.success("Объявление успешно обновлено");


  } catch (error) {
    toast.error("Ошибка при обновлении листинга");
    console.error(error);
  } finally {
    setIsSubmitting(false);
    
  }
};



  if (!formData) return <p>Загрузка...</p>;

  return (
    <ListingField>
    <ProfileTitle bgc={colors.btnMainColor}>Обновление объявления <button style={{position: 'relative' , right:'-10%' }} onClick={onClose}>X</button></ProfileTitle>
    <>
     <ListingLabel>Имя листинга  <span style={{ color: "red" }}> *</span></ListingLabel>
      <input value={formData.title} onChange={e => setFormData({ ...formData, title: e.target.value })} placeholder="Заголовок" />
      <ListingLabel>Описание  <span style={{ color: "red" }}> *</span></ListingLabel>
      <textarea value={formData.description} className='textarea' onChange={e => setFormData({ ...formData, description: e.target.value })} rows={4} placeholder="Описание" />
      <ListingLabel>Цена  <span style={{ color: "red" }}> *</span></ListingLabel>
      <input
      type="number"
      value={formData.price}
      onChange={e => setFormData({ ...formData, price: Number(e.target.value) })}
      placeholder="Цена"
    />
      <ListingLabel>Главное фото</ListingLabel>
      <input type="file" onChange={handleFileChange} />
      <ListingField>
      <ListingLabel>Галерея (несколько фото)</ListingLabel>
        <input type="file" accept="image/*" multiple onChange={handleMultipleFileChange} />
        {selectedFiles.length > 0 && (
          <ul>
            {selectedFiles.map((file, index) => (
              <li key={index}>{file.name}</li>
            ))}
          </ul>
        )}
      </ListingField>
      <ListingLabel>Видео</ListingLabel>
      <input type="file" onChange={handleVideoChange} />
      </>
      {fields?.fields?.map((field: IField) => (
  <div key={field.name}>
    <ListingLabel>
      {field.name}
      {field?.required && <span style={{ color: 'red' }}> *</span>}
    </ListingLabel>

    {field.type === 'File' ? (
      <input
        type="file"
        onChange={(e) => handleDynamicFileChange(field.name, e)}
      />
    ) : field.type === 'Boolean' ? (
      <select
        value={String(formData.fields[field.name] ?? 'false')}
        onChange={(e) => handleFieldChange(field.name, e.target.value)}
      >
        <option value="false">Нет</option>
        <option value="true">Да</option>
      </select>
    ) : field.type === 'Double' ? (
      <input
        type="number"
        step="0.01"
        value={formData.fields[field.name]?.toString() || ''}
        onChange={(e) => handleFieldChange(field.name, e.target.value)}
        placeholder={`${field.name} (дробное число)`}
      />
    ) : field.type === 'List' && listFieldOptions[field.name] ? (
       <FormGroup>
    {listFieldOptions[field.name].map((option) => (
      <FormControlLabel
        key={option}
        control={
          <Checkbox
          checked={
            Array.isArray(formData.fields[field.name]) &&
            (formData.fields[field.name] as string[]).includes(option)
          }
          
            onChange={(e) =>
              handleMultiSelectChange(field.name, option, e.target.checked)
            }
          />
        }
        label={option}
      />
    ))}
  </FormGroup>
    ) : (
      <input
        type="text"
        value={formData.fields[field.name]?.toString() || ''}
        onChange={e => handleFieldChange(field.name, e.target.value)}
        placeholder={field.name}
      />
    )}
  </div>
))}

      

      <ListingButton onClick={handleUpdate} disabled={isSubmitting}>
        {isSubmitting ? 'Обновление...' : 'Обновить'}
      </ListingButton>
    </ListingField>
  );
};
