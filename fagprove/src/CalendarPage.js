

import React, { useContext, useEffect } from 'react';
import { IsAdminContext, superRequest } from './App';
import { FlexDiv, FormLabel, TextSmall } from './BasicStyles';

import styled from 'styled-components';
import { Button } from './Button';
import { Link } from 'react-router-dom';
import { EditExaminationPage } from './EditExemenationPage';
import DatePicker from 'react-datepicker/es';




const Cell = styled.div`
width: 4em;
box-sizing: border-box;
display: flex;
padding: .5em;
align-items: center;
justify-content: center;
`;


const ExaminationDiv = styled.div `
  background-color: yellow;
  width: 100%;
  :hover {
    cursor: pointer;
  }
`;
const NoDataDiv = styled.div`


:hover {
cursor: pointer;
}
`;






export function CalendarPage() {
  const [data, setData] = React.useState();
  const [fromDate, setFromDate] = React.useState(new Date().setDate(1));
  const [toDate, setToDate] = React.useState(new Date().setDate((new Date().getDate()+30)));
  const [editData, setEdit] = React.useState({
    isEditing: false,
    id: undefined,
  });
  const adminConsumer = useContext(IsAdminContext);
  const hasPemission = adminConsumer.role === 'ROLE_ADMIN' || adminConsumer.role === 'ROLE_MANAGER';
  const isAdmin = adminConsumer.role === 'ROLE_ADMIN';
  // async function GetExaminations() {
  //   const fetch = await superRequest('http://localhost:8080/examination/list', {
  //   });
  //   const res = await fetch;
  //
  //   console.log(res);
  //
  // }
  async function getExaminators() {
    const fetch = await superRequest(' http://localhost:8080/examination/calendar', {
      "startDate": fromDate,
      "endDate": toDate
    });
    const res = await fetch;

    setData(res);

  }

  function setDateAsBusy(date, person) {

    const fromDate = new Date(date.year, date.month, date.day+1);
    const toDate = new Date(date.year, (date.month), (date.day+1));
      superRequest('http://localhost:8080/person/setBusy', {
        "person": person.id,
        "from": fromDate,
        "to": toDate,
      });
      window.location.reload();


  }


  useEffect(() => {
    // GetExaminations();
    getExaminators();
  }, [fromDate, toDate]);
  return (
    <div style={{width: '100%', margin: '0 auto'}}>
      {editData.isEditing ?
        <div style={{width: '100%' }}>
        <EditExaminationPage id={editData.id}/>
        </div> :

        <div>
          <FlexDiv style={{justifyContent: 'space-between',marginBottom: '5em'}}>
            <FlexDiv>
              <div>
                <FormLabel>Fra:</FormLabel>

              <DatePicker onSelect={(date)=> {
                setFromDate(date);
              }}  selected={fromDate}/>
              </div>
              <div style={{marginLeft: '1em'}}>
                <FormLabel>Til:</FormLabel>
              <DatePicker onSelect={(date)=> {
                setToDate(date);
              }} selected={toDate} />
              </div>
              </FlexDiv>
            {hasPemission &&
            <Link to={'/calendar/create/examination'}>
              <Button >Ny eksemenasjon</Button>
            </Link>
            }

          </FlexDiv>
          <div style={{margin: '0 auto', width: '90%'}}>
            <FlexDiv style={{}}>
              <Cell>Uke</Cell>
              <Cell>Veke dag</Cell>
              <Cell>år</Cell>
              <Cell>dato</Cell>
              <FlexDiv style={{marginLeft: '1em'}}>
                {data && data[0].examinators.map(examinator => {
                  return (
                    <FlexDiv style={{}}>
                      <Cell style={{width: '8em'}}>{examinator.name}</Cell>
                    </FlexDiv>
                  );
                })}
              </FlexDiv>
            </FlexDiv>
          <div style={{maxHeight: '60vh', overflow: 'auto'}} >
            {data && data.map((c) => {
              return (
                <FlexDiv style={{borderBottom: '1px solid #333'}}>
                  <div>
                    <div key={c.day}>
                      <FlexDiv style={{
                        marginRight: '1em'
                      }}>
                        <Cell>
                          {c.week}
                        </Cell>
                        <Cell style={{color: (c.dayOfWeek === 6 || c.dayOfWeek === 7) ? 'red': '#333'}}>
                          {c.dayOfWeek}
                        </Cell>
                        <Cell>
                          {c.year}
                        </Cell>
                        <Cell>
                          {c.day}.{(c.month+1)}
                        </Cell>
                      </FlexDiv>
                    </div>
                  </div> {c.examinators.map(ex => {
                  return (
                    <Cell style={{width: '8em', borderLeft: '1px solid #333'}}>
                      {ex.isBusy ?
                        <FlexDiv style={{width: '100%', justifyContent: 'center', height: '2em', backgroundColor: 'red', color: '#fff'}}>Opptatt</FlexDiv>:
                        ex.examinations && ex.examinations.length > 0 ?
                          <ExaminationDiv onClick={()=> {
                            if(hasPemission) {
                              setEdit({
                                isEditing: true,
                                id: ex.examinations[0].id,
                              });
                            } else {
                              alert('Du har ikkje tilgang til å redigere denne');
                            }

                          }}>
                              <TextSmall>{ex.examinations[0].candidate.name}</TextSmall>
                              <TextSmall>{ex.examinations[0].candidate.region}</TextSmall>
                              <TextSmall style={{color: 'red'}}>{ex.examinations[0].responsibleExaminator.name}</TextSmall>
                            </ExaminationDiv>: <NoDataDiv style={{height: '2em', width: '100%'}}
                            onClick={()=> {
                              if(isAdmin || adminConsumer.me.id === ex.id) {
                                if (window.confirm('sett som opptatt?')) {
                                  setDateAsBusy(c, ex);
                                }
                              } else {
                                alert('Du har ikkje tilgang til å sette andre som opptatt');
                              }
                            }}
                          />
                      }

                    </Cell>
                  );
                })}
                </FlexDiv>
              );
            })}
          </div>

          </div>
        </div>
      }































    </div>
  )
}