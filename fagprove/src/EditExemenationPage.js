import React, { useEffect } from 'react';
import { superRequest } from './App';
import { Dropdown } from './Dropdown';
import { FlexDiv, FormLabel, TextSmall } from './BasicStyles';

import DatePicker, { registerLocale } from "react-datepicker";
import nb from 'date-fns/locale/nb';

import "react-datepicker/dist/react-datepicker.css";
import { Button } from './Button';
import Redirect from 'react-router-dom/es/Redirect';












export function EditExaminationPage(props) {
  const [candidates, setCandidates] = React.useState();
  const [examinators, setExaminators] = React.useState();


  const [showError, setShowError] = React.useState('');
  const [formData, setFormData] = React.useState({
    startDate: new Date(),
    endDate: new Date(),
  });

  function formatStartDate(date) {
   return new Date(date.getFullYear(), date.getMonth(), date.getDate(), 12)
  }
  function formatEndDate(date) {
   return new Date(formData.endDate.getFullYear(), formData.endDate.getMonth(), formData.endDate.getDate(), 12)
  }

  async function getCandidates() {
    const fetch = await superRequest('http://localhost:8080/person/listCandidates', {
    });
    const res = await fetch;

    setCandidates(res);
  }
  async function getExamination() {
    const fetch = await superRequest('http://localhost:8080/examination/show/'+props.id, {
    });
    const res = await fetch;
    setFormData({
      candidate:{value: res.candidate.id, label: res.candidate.name},
      responsibleExaminator: {value: res.responsibleExaminator.id, label:res.responsibleExaminator.name },
      secondaryExaminator:  {value: res.secondaryExaminator.id, label:res.secondaryExaminator.name },
      startDate: new Date(res.startDate),
      endDate: new Date(res.endDate),
    });
  }
  async function getExaminators() {
    const fetch = await superRequest('http://localhost:8080/person/listExaminators', {

    });
    const res = await fetch;

    setExaminators(res);
  }


  useEffect(() => {
    getExaminators();
    getCandidates();
    getExamination();
  }, []);

  registerLocale('nb', nb);
  return (
    <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center'}}>
      <div style={{width: '100%', display: 'flex', justifyContent: 'flex-end'}}>
        <Button onClick={()=> {
          window.location.reload();
        }}>Attende</Button>
      </div>
      {formData.candidate &&
      <form onSubmit={async (e)=> {
        e.preventDefault();
        if(formData.candidate ===undefined || formData.responsibleExaminator === undefined || formData.secondaryExaminator === undefined) {
          setShowError(true);
        } else {
          await superRequest('http://localhost:8080/examination/update', {
            id: props.id,
            candidate: formData.candidate.value,
            responsibleExaminator: formData.responsibleExaminator.value,
            secondaryExaminator: formData.secondaryExaminator.value,
            startDate: new Date(formData.startDate.getFullYear(), formData.startDate.getMonth(), formData.startDate.getDate(), 6),
            endDate: new Date(formData.endDate.getFullYear(), formData.endDate.getMonth(), formData.endDate.getDate(), 12)
          }).then(c => {
            if (typeof c === 'object') {
              window.location.reload();
            } else {
              setShowError(c);
            }
          }).catch(error => {

          });
        }
      }}>
        <div>
          <FormLabel>Kandidat</FormLabel>
          <Dropdown value={formData.candidate} onChange={(candidate)=> {
            setFormData({
              ...formData,
              candidate : candidate
            })
          }} multi={false} items={candidates && candidates?.map(c => {
            return (
              {value: c.id, label: c.name}
            );
          })}/>
        </div>
        <div style={{marginTop: '1em'}}>
          <FormLabel>Ansvarlig eksamninator</FormLabel>
          <Dropdown  value={formData.responsibleExaminator}onChange={(value)=> {
            setFormData({
              ...formData,
              responsibleExaminator: value[0]
            })
          }} multi={false} items={examinators?.map(c => {
            return (
              {value: c.id, label: c.name}
            );
          })}/>
          <div style={{marginTop: '1em'}}>
            <FormLabel>sekunder eksamninator</FormLabel>
            <Dropdown  value={formData.secondaryExaminator} onChange={(value)=> {
              setFormData({
                ...formData,
                secondaryExaminator : value[0]
              })
            }} multi={false} items={examinators?.map(c => {
              return (
                {value: c.id, label: c.name}
              );
            })}/>
          </div>
          <div style={{marginTop: '1em'}}>
            <FormLabel>start dato</FormLabel>
            <DatePicker
              selected={formData.startDate}
              onSelect={(date) => setFormData({...formData, startDate: date}) }
              locale="nb"
            />
          </div>
          <div style={{marginTop: '1em'}}>
            <FormLabel>slutt dato</FormLabel>
            <DatePicker
              selected={formData.endDate}
              onSelect={(date) => setFormData({...formData, endDate: date}) }
              locale="nb"
            />
          </div>
          <FlexDiv>
          <Button style={{marginTop: '1em'}} type={'submit'}>Rediger</Button>
          <Button style={{marginTop: '1em', marginLeft: '2em', backgroundColor: 'red'}} onClick={async (e)=> {
            e.preventDefault();
            await superRequest('http://localhost:8080/examination/delete/'+props.id, {
            }).then(c => {
            }).catch(error => {
              console.log(error);
              setTimeout(()=> {
                // window.location.reload();
              }, 500);
            });
          }} >slett</Button>
          </FlexDiv>
        </div>

        {showError.length > 0 &&
        <TextSmall style={{color: 'red', marginTop: '.5em'}}>{showError}</TextSmall>
        }
      </form>
      }


    </div>
  )
}











































